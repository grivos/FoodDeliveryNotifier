package com.invariantlabs.fooddeliverynotifier.domain.model

import android.os.Parcelable
import com.invariantlabs.presentation.mvi.*
import kotlinx.android.parcel.Parcelize

sealed class RestaurantsIntent : MviIntent<RestaurantsAction> {
    object InitialIntent : RestaurantsIntent()
    data class ClickIntent(val restaurant: Restaurant) : RestaurantsIntent()
    data class ClickFavoriteIntent(val restaurant: Restaurant) : RestaurantsIntent()
    data class ClickMonitorIntent(val restaurant: Restaurant) : RestaurantsIntent()
    object ClickSearchIntent : RestaurantsIntent()
    object PullToRefreshIntent : RestaurantsIntent()
    object TryAgainIntent : RestaurantsIntent()
    object CloseSearchIntent : RestaurantsIntent()
    data class EnterSearchQueryIntent(val query: String) : RestaurantsIntent()
    data class SelectedLocationIntent(val location: StoredLocation) : RestaurantsIntent()
    object ChangeLocationIntent : RestaurantsIntent()

    override fun mapToAction(): RestaurantsAction = when (this) {
        InitialIntent -> RestaurantsAction.InitialAction
        is ClickIntent -> RestaurantsAction.ClickRestaurantAction(restaurant)
        is ClickFavoriteIntent -> RestaurantsAction.ClickFavoriteAction(restaurant)
        is ClickMonitorIntent -> RestaurantsAction.ClickMonitorAction(restaurant)
        ClickSearchIntent -> RestaurantsAction.ClickSearchAction
        PullToRefreshIntent -> RestaurantsAction.ReloadAction
        TryAgainIntent -> RestaurantsAction.ReloadAction
        CloseSearchIntent -> RestaurantsAction.CloseSearchAction
        is EnterSearchQueryIntent -> RestaurantsAction.EnterSearchQueryAction(query)
        is SelectedLocationIntent -> RestaurantsAction.SelectedLocationAction(location)
        ChangeLocationIntent -> RestaurantsAction.ChangeLocationAction
    }
}

sealed class RestaurantsAction : MviAction {
    object InitialAction : RestaurantsAction()
    data class ClickRestaurantAction(val restaurant: Restaurant) : RestaurantsAction()
    data class ClickFavoriteAction(val restaurant: Restaurant) : RestaurantsAction()
    data class ClickMonitorAction(val restaurant: Restaurant) : RestaurantsAction()
    object ClickSearchAction : RestaurantsAction()
    object ReloadAction : RestaurantsAction()
    object CloseSearchAction : RestaurantsAction()
    data class EnterSearchQueryAction(val query: String) : RestaurantsAction()
    data class SelectedLocationAction(val location: StoredLocation) : RestaurantsAction()
    object ChangeLocationAction : RestaurantsAction()
}

sealed class RestaurantsResult : MviResult {
    data class SetRestaurantsResult(val restaurants: List<Restaurant>) : RestaurantsResult()
    object ShowSearchResult : RestaurantsResult()
    object ErrorResult : RestaurantsResult()
    object ShowLoadingResult : RestaurantsResult()
    object CloseSearchResult : RestaurantsResult()
    data class OpenRestaurantResult(val restaurant: Restaurant) : RestaurantsResult()
    data class GoToSearchResult(val query: String) : RestaurantsResult()
    data class StoredLocationResult(val storedLocation: StoredLocation) : RestaurantsResult()
    data class ChangeLocationActionResult(val storedLocation: StoredLocation) : RestaurantsResult()
}

@Parcelize
data class RestaurantsViewState(
    val restaurants: List<Restaurant> = emptyList(),
    val showLoading: Boolean = true,
    val openRestaurant: ViewStateEvent<Restaurant>? = null,
    val showSearch: Boolean = false,
    val type: Type = Type.Items,
    val goToSearchResults: ViewStateEvent<SearchQuery>? = null,
    val storedLocation: StoredLocation? = null,
    val goToChangeLocation: ViewStateEvent<StoredLocation>? = null,
) : MviViewState, Parcelable {

    enum class Type {
        Empty, Items, Error
    }

    companion object {

        val reducer: Reducer<RestaurantsViewState, RestaurantsResult> = { state, result ->
            when (result) {
                is RestaurantsResult.SetRestaurantsResult -> state.copy(
                    restaurants = result.restaurants,
                    showLoading = false,
                    type = if (result.restaurants.isEmpty()) Type.Empty else Type.Items
                )
                RestaurantsResult.ShowSearchResult -> state.copy(showSearch = true)
                is RestaurantsResult.OpenRestaurantResult -> state.copy(
                    openRestaurant = ViewStateEvent(
                        result.restaurant
                    )
                )
                RestaurantsResult.ErrorResult -> state.copy(showLoading = false, type = Type.Error)
                is RestaurantsResult.GoToSearchResult -> state.copy(
                    showSearch = false,
                    goToSearchResults = ViewStateEvent(SearchQuery(result.query))
                )
                RestaurantsResult.ShowLoadingResult -> state.copy(showLoading = true)
                RestaurantsResult.CloseSearchResult -> state.copy(showSearch = false)
                is RestaurantsResult.StoredLocationResult -> state.copy(storedLocation = result.storedLocation)
                is RestaurantsResult.ChangeLocationActionResult -> state.copy(
                    goToChangeLocation = ViewStateEvent(
                        result.storedLocation
                    )
                )
            }
        }
    }
}

enum class RestaurantScreen {
    Search, Favorites, Monitored
}
