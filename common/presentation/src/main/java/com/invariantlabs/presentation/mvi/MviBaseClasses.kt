package com.invariantlabs.presentation.mvi

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.invariantlabs.presentation.extensions.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

interface MviViewModel<I : MviIntent<A>, A : MviAction, S : MviViewState> {

    val states: Observable<S>

    fun processIntents(intents: Observable<I>)
    fun stopProcessingIntents()
}

interface MviView<I : MviIntent<A>, A : MviAction, S : MviViewState> {

    val intents: Observable<I>

    fun render(state: S)
}

open class BaseMviViewModel<I : MviIntent<A>, A : MviAction, R : MviResult, S : MviViewState>(
    private val handle: SavedStateHandle,
    private val processor: MviActionProcessor<A, R>,
    private val initialIntent: I,
    initialState: S,
    reducer: Reducer<S, R>
) : MviViewModel<I, A, S>, ViewModel() {

    private val shouldSaveState = initialState is Parcelable
    private var intentsDisposable: Disposable? = null
    private var internalDisposable: Disposable? = null

    // Filter out initial intent if emitted more than once - this helps bypass device rotation
    // issues where intent is emitted twice
    private val intentFilter: ObservableTransformer<I, I>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.merge(
                    shared.ofType(initialIntent::class.java).take(1),
                    shared.notOfType(initialIntent::class.java)
                )
            }
        }

    // Proxy subject that keeps the stream alive during config changes
    private val intentSink: PublishSubject<I> = PublishSubject.create()

    private val statesSubject = BehaviorSubject.create<S>()

    init {
        val firstState = if (shouldSaveState) handle.get(KEY_STATE) ?: initialState else initialState
        internalDisposable = intentSink
            .startWith(initialIntent)
            .compose(intentFilter)
            .map { it.mapToAction() }
            .compose { processor.apply(it) }
            .doOnNext { result -> onNewResult(result) }
            .scan(firstState, reducer)
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .subscribe(
                { state ->
                    internalLogger(state)
                    if (shouldSaveState) {
                        handle[KEY_STATE] = state
                    }
                    statesSubject.onNext(state)
                },
                { error ->
                    crashHandler(error)
                }
            )
    }

    override val states: Observable<S>
        get() = statesSubject
            .observeOn(AndroidSchedulers.mainThread())

    final override fun processIntents(intents: Observable<I>) {
        intentsDisposable = intents.subscribe { intent ->
            Timber.d("intent = $intent")
            onUserIntent(intent)
            intentSink.onNext(intent)
        }
    }

    protected open fun onUserIntent(intent: I) {
        // no-op
    }

    protected open fun onNewResult(result: R) {
        // no-op
    }

    override fun stopProcessingIntents() {
        intentsDisposable?.dispose()
    }

    // Log all ViewStates
    private fun internalLogger(state: S) = Timber.i("state = $state")

    // Raise any unhandled exceptions
    private fun crashHandler(throwable: Throwable): Unit = throw throwable

    override fun onCleared() {
        internalDisposable?.dispose()
        processor.onDispose()
    }
}

private const val KEY_STATE = "state"
