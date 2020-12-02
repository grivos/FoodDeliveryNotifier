package com.invariantlabs.presentation.mvi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Used to represent a one-shot UI event within an [MviViewState], so that we don't have to
 * toggle [Boolean] values or use timers in Rx or anything too wild. [consume] allows you to
 * process the event only once.
 *
 * Can store whatever data you might want - most of the time this would be a [String] or
 * res ID [Int].
 */
@Parcelize
data class ViewStateEvent<T : Parcelable>(
    val payload: T? = null,
    private val isConsumed: AtomicBoolean = AtomicBoolean(
        false
    )
) : Parcelable {

    /**
     * Allows you to consume the [payload] of the event only once, as it will be marked as
     * consumed on access.
     */
    fun consume(action: (T?) -> Unit) {
        if (!isConsumed.getAndSet(true)) {
            action(payload)
        }
    }
}
