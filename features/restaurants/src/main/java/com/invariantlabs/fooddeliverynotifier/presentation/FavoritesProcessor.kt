package com.invariantlabs.fooddeliverynotifier.presentation

import com.invariantlabs.fooddeliverynotifier.data.repository.LocationStore
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsResult
import com.invariantlabs.fooddeliverynotifier.service.WoltService
import io.reactivex.Observable
import timber.log.Timber

class FavoritesProcessor(
    woltService: WoltService,
    locationStore: LocationStore,
) : BaseRestaurantProcessor(woltService, locationStore) {

    override fun getRestaurantsObservable(): Observable<RestaurantsResult> {
        return woltService.getFavorites()
            .map { list -> RestaurantsResult.SetRestaurantsResult(list) }
            .cast(RestaurantsResult::class.java)
            .onErrorReturn { error ->
                Timber.e(error, "Error getting favorites")
                RestaurantsResult.ErrorResult
            }
            .startWith(RestaurantsResult.ShowLoadingResult)
    }
}
