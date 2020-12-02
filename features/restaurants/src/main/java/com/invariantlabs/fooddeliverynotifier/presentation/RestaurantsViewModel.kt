package com.invariantlabs.fooddeliverynotifier.presentation

import androidx.lifecycle.SavedStateHandle
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsAction
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsIntent
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsResult
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsViewState
import com.invariantlabs.presentation.mvi.BaseMviViewModel
import com.invariantlabs.presentation.mvi.MviActionProcessor

class RestaurantsViewModel(
    handle: SavedStateHandle,
    processor: MviActionProcessor<RestaurantsAction, RestaurantsResult>
) : BaseMviViewModel<RestaurantsIntent, RestaurantsAction, RestaurantsResult, RestaurantsViewState>(
    handle,
    processor,
    RestaurantsIntent.InitialIntent,
    RestaurantsViewState(),
    RestaurantsViewState.reducer
)
