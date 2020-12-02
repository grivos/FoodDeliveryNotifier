package com.invariantlabs.fooddeliverynotifier.presentation

import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsWrapperAction
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsWrapperResult
import com.invariantlabs.presentation.mvi.MviActionProcessor
import io.reactivex.Observable

class RestaurantsWrapperProcessor :
    MviActionProcessor<RestaurantsWrapperAction, RestaurantsWrapperResult>() {

    override fun getActionProcessors(shared: Observable<RestaurantsWrapperAction>): List<Observable<RestaurantsWrapperResult>> =
        listOf(
            shared.connect { actions ->
                actions.flatMap { action ->
                    when (action) {
                        RestaurantsWrapperAction.InitialAction -> Observable.empty()
                        is RestaurantsWrapperAction.EnterSearchQueryAction -> Observable.just(
                            RestaurantsWrapperResult.GoToSearchResult(action.query)
                        )
                        RestaurantsWrapperAction.ClickSearchAction -> Observable.just(
                            RestaurantsWrapperResult.ShowSearchResult
                        )
                        RestaurantsWrapperAction.CloseSearchAction -> Observable.just(
                            RestaurantsWrapperResult.CloseSearchResult
                        )
                    }
                }
            }
        )
}
