package com.invariantlabs.fooddeliverynotifier.presentation

import com.invariantlabs.fooddeliverynotifier.data.repository.LocationStore
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsAction
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsResult
import com.invariantlabs.fooddeliverynotifier.service.WoltService
import com.invariantlabs.presentation.mvi.MviActionProcessor
import io.reactivex.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class BaseRestaurantProcessor(
    protected val woltService: WoltService,
    private val locationStore: LocationStore,
) : MviActionProcessor<RestaurantsAction, RestaurantsResult>() {

    final override fun getActionProcessors(shared: Observable<RestaurantsAction>): List<Observable<RestaurantsResult>> =
        listOf(
            shared.connect { actions ->
                actions.flatMap { action ->
                    when (action) {
                        RestaurantsAction.InitialAction,
                        RestaurantsAction.ReloadAction -> getRestaurantsObservable()
                        is RestaurantsAction.ClickRestaurantAction -> handleClickRestaurant(action)
                        is RestaurantsAction.ClickMonitorAction -> handleClickMonitor(action)
                        RestaurantsAction.ClickSearchAction -> handleClickShowSearch()
                        is RestaurantsAction.ClickFavoriteAction -> handleClickFavorite(action)
                        is RestaurantsAction.EnterSearchQueryAction -> handleEnterSearchQuery(action)
                        RestaurantsAction.CloseSearchAction -> handleCloseSearch()
                        is RestaurantsAction.SelectedLocationAction -> handleSelectLocation(action)
                        RestaurantsAction.ChangeLocationAction -> handleClickChangeLocation()
                    }
                }
            },
            locationStore.storedLocationObservable.map { storedLocation ->
                RestaurantsResult.StoredLocationResult(storedLocation)
            }
        )

    private fun handleClickChangeLocation(): Observable<RestaurantsResult.ChangeLocationActionResult> {
        return locationStore.storedLocationObservable
            .take(1)
            .map { currentLocation ->
                RestaurantsResult.ChangeLocationActionResult(currentLocation)
            }
    }

    private fun handleSelectLocation(action: RestaurantsAction.SelectedLocationAction): Observable<RestaurantsResult> {
        GlobalScope.launch {
            locationStore.setStoredLocation(action.location)
        }
        return Observable.empty()
    }

    private fun handleCloseSearch() = Observable.just(RestaurantsResult.CloseSearchResult)

    private fun handleEnterSearchQuery(action: RestaurantsAction.EnterSearchQueryAction) =
        Observable.just(RestaurantsResult.GoToSearchResult(action.query))

    private fun handleClickFavorite(action: RestaurantsAction.ClickFavoriteAction): Observable<RestaurantsResult> {
        woltService.toggleFavorite(action.restaurant)
        return Observable.empty()
    }

    private fun handleClickShowSearch() = Observable.just(RestaurantsResult.ShowSearchResult)

    private fun handleClickMonitor(action: RestaurantsAction.ClickMonitorAction): Observable<RestaurantsResult>? {
        woltService.toggleMonitored(action.restaurant)
        return Observable.empty()
    }

    private fun handleClickRestaurant(action: RestaurantsAction.ClickRestaurantAction): Observable<RestaurantsResult.OpenRestaurantResult> {
        return Observable.just(
            RestaurantsResult.OpenRestaurantResult(
                action.restaurant
            )
        )
    }

    protected abstract fun getRestaurantsObservable(): Observable<RestaurantsResult>
}
