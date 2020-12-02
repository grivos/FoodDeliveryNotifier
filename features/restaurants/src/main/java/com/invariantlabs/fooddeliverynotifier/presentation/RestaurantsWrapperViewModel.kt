package com.invariantlabs.fooddeliverynotifier.presentation

import androidx.lifecycle.SavedStateHandle
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsWrapperAction
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsWrapperIntent
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsWrapperResult
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsWrapperViewState
import com.invariantlabs.presentation.mvi.BaseMviViewModel
import com.invariantlabs.presentation.mvi.MviActionProcessor

class RestaurantsWrapperViewModel(
    handle: SavedStateHandle,
    processor: MviActionProcessor<RestaurantsWrapperAction, RestaurantsWrapperResult>
) : BaseMviViewModel<RestaurantsWrapperIntent, RestaurantsWrapperAction, RestaurantsWrapperResult, RestaurantsWrapperViewState>(
    handle,
    processor,
    RestaurantsWrapperIntent.InitialIntent,
    RestaurantsWrapperViewState(),
    RestaurantsWrapperViewState.reducer
)
