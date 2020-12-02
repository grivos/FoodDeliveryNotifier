package com.invariantlabs.presentation.mvi

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.invariantlabs.presentation.base.BaseFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

abstract class BaseMviFragment<VB : ViewBinding, I : MviIntent<A>, A : MviAction, S : MviViewState>(
    @LayoutRes layout: Int,
    viewBindingFactory: (View) -> VB
) : BaseFragment<VB>(
    layout,
    viewBindingFactory
),
    MviView<I, A, S> {

    private val compositeDisposable = CompositeDisposable()
    abstract val model: MviViewModel<I, A, S>
    private lateinit var state: S

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.processIntents(intents)
    }

    override fun onDestroyView() {
        model.stopProcessingIntents()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        compositeDisposable +=
            model.states
                .subscribeBy(
                    onNext = { state ->
                        this.state = state
                        render(state)
                    }
                )
    }

    override fun onPause() {
        compositeDisposable.clear()
        super.onPause()
    }
}
