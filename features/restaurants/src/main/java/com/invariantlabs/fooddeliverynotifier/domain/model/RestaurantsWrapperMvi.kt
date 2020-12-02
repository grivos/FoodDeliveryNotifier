package com.invariantlabs.fooddeliverynotifier.domain.model

import android.os.Parcelable
import com.invariantlabs.presentation.mvi.*
import kotlinx.android.parcel.Parcelize

sealed class RestaurantsWrapperIntent : MviIntent<RestaurantsWrapperAction> {
    object InitialIntent : RestaurantsWrapperIntent()
    object ClickSearchIntent : RestaurantsWrapperIntent()
    data class EnterSearchQueryIntent(val query: String) : RestaurantsWrapperIntent()
    object CloseSearchIntent : RestaurantsWrapperIntent()

    override fun mapToAction(): RestaurantsWrapperAction = when (this) {
        InitialIntent -> RestaurantsWrapperAction.InitialAction
        ClickSearchIntent -> RestaurantsWrapperAction.ClickSearchAction
        is EnterSearchQueryIntent -> RestaurantsWrapperAction.EnterSearchQueryAction(query)
        CloseSearchIntent -> RestaurantsWrapperAction.CloseSearchAction
    }
}

sealed class RestaurantsWrapperAction : MviAction {
    object InitialAction : RestaurantsWrapperAction()
    object ClickSearchAction : RestaurantsWrapperAction()
    data class EnterSearchQueryAction(val query: String) : RestaurantsWrapperAction()
    object CloseSearchAction : RestaurantsWrapperAction()
}

sealed class RestaurantsWrapperResult : MviResult {
    object ShowSearchResult : RestaurantsWrapperResult()
    data class GoToSearchResult(val query: String) : RestaurantsWrapperResult()
    object CloseSearchResult : RestaurantsWrapperResult()
}

@Parcelize
data class RestaurantsWrapperViewState(
    val showSearch: Boolean = false,
    val goToSearchResults: ViewStateEvent<SearchQuery>? = null,
) : MviViewState, Parcelable {

    companion object {

        val reducer: Reducer<RestaurantsWrapperViewState, RestaurantsWrapperResult> =
            { state, result ->
                when (result) {
                    RestaurantsWrapperResult.ShowSearchResult -> state.copy(showSearch = true)
                    is RestaurantsWrapperResult.GoToSearchResult -> state.copy(
                        showSearch = false,
                        goToSearchResults = ViewStateEvent(SearchQuery(result.query))
                    )
                    RestaurantsWrapperResult.CloseSearchResult -> state.copy(showSearch = false)
                }
            }
    }
}
