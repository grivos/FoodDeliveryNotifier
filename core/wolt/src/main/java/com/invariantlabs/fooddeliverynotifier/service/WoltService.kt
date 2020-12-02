package com.invariantlabs.fooddeliverynotifier.service

import com.invariantlabs.fooddeliverynotifier.api.WoltApi
import com.invariantlabs.fooddeliverynotifier.data.DeliveryStatusExtractor
import com.invariantlabs.fooddeliverynotifier.data.dto.WoltRestaurantDto
import com.invariantlabs.fooddeliverynotifier.data.dto.WoltSlugResultDto
import com.invariantlabs.fooddeliverynotifier.data.repository.LocationStore
import com.invariantlabs.fooddeliverynotifier.datasource.model.RestaurantDao
import com.invariantlabs.fooddeliverynotifier.domain.Database
import com.invariantlabs.fooddeliverynotifier.domain.model.DeliveryStatus
import com.invariantlabs.fooddeliverynotifier.domain.model.Restaurant
import com.invariantlabs.fooddeliverynotifier.service.RestaurantsPredicate.Favorite
import com.invariantlabs.fooddeliverynotifier.service.RestaurantsPredicate.Monitored
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.rx.asObservable
import io.reactivex.Observable
import io.reactivex.rxkotlin.combineLatest
import io.reactivex.schedulers.Schedulers
import java.util.*

class WoltService(
    private val woltApi: WoltApi,
    private val database: Database,
    private val locationStore: LocationStore,
) {

    fun search(query: String): Observable<List<Restaurant>> {
        val favoritesObservable = database.restaurantQueries.selectAllFavorites().asObservable()
        val monitoredObservable = database.restaurantQueries.selectAllMonitored().asObservable()
        val searchObservable = locationStore.storedLocationObservable
            .flatMap { location ->
                woltApi.getSearchResult(query, location.lat, location.lng)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
            }
        return Observable.combineLatest(
            favoritesObservable,
            searchObservable,
            monitoredObservable
        ) { favoritesQuery, resultDto, monitoredQuery ->
            val favoriteMap =
                favoritesQuery.executeAsList().map { it.slug to it.favorite_timestamp }.toMap()
            val monitoredMap =
                monitoredQuery.executeAsList().map { it.slug to it.monitored_timestamp }.toMap()
            resultDto.restaurants.map { dto ->
                val value = dto.value
                mapRestaurant(value, favoriteMap, monitoredMap)
            }
        }
    }

    fun getFavorites(): Observable<List<Restaurant>> = getRestaurantsByPredicate(Favorite)

    fun getMonitored(): Observable<List<Restaurant>> = getRestaurantsByPredicate(Monitored)

    private fun getRestaurantsByPredicate(predicate: RestaurantsPredicate): Observable<List<Restaurant>> {
        val favoritesObservable = database.restaurantQueries.selectAllFavorites().asObservable()
        val monitoredObservable = database.restaurantQueries.selectAllMonitored().asObservable()
        val cache = mutableMapOf<String, WoltSlugResultDto>()
        return Observable.combineLatest(
            favoritesObservable,
            monitoredObservable
        ) { favorites, monitored ->
            FavoritesAndMonitored(favorites, monitored)
        }
            .switchMap { (favoritesQuery, monitoredQuery) ->
                val favoriteMap =
                    favoritesQuery.executeAsList().map { it.slug to it.favorite_timestamp }.toMap()
                val monitoredMap =
                    monitoredQuery.executeAsList().map { it.slug to it.monitored_timestamp }.toMap()
                val setToIterate = when (predicate) {
                    Monitored -> monitoredMap.keys
                    Favorite -> favoriteMap.keys
                }
                if (setToIterate.isNotEmpty()) {
                    val observablesList = setToIterate.map { slug ->
                        val cached = cache[slug]
                        if (cached != null) {
                            Observable.just(cached)
                        } else {
                            woltApi.getRestaurantSingle(slug)
                                .subscribeOn(Schedulers.io())
                                .toObservable()
                        }
                    }
                    observablesList.combineLatest { resultList ->
                        resultList.map { dto ->
                            val restaurantDto = dto.restaurants.first()
                            cache[restaurantDto.slug] = dto
                            mapRestaurant(restaurantDto, favoriteMap, monitoredMap)
                        }
                    }
                } else {
                    Observable.just(emptyList())
                }
            }
    }

    suspend fun getRestaurantStatus(slug: String): DeliveryStatus {
        val dto = woltApi.getRestaurant(slug)
        return DeliveryStatusExtractor(dto.restaurants.first()).extractStatus()
    }

    private fun mapRestaurant(
        restaurantDto: WoltRestaurantDto,
        favorites: Map<String, Long?>,
        monitored: Map<String, Long?>,
    ): Restaurant {
        val userLanguage = Locale.getDefault().language.let { currentLocale ->
            if (currentLocale == LOCALE_HEBREW) WOLT_HEBREW else currentLocale
        }
        return Restaurant(
            name = (
                    restaurantDto.name.firstOrNull { it.language == userLanguage }
                        ?: restaurantDto.name.first()
                    ).value,
            imageUrl = restaurantDto.imageUrl,
            deliveryStatus = DeliveryStatusExtractor(restaurantDto).extractStatus(),
            favoriteTime = favorites[restaurantDto.slug],
            slug = restaurantDto.slug,
            publicUrl = restaurantDto.publicUrl,
            monitoredTime = monitored[restaurantDto.slug],
        )
    }

    fun toggleFavorite(restaurant: Restaurant) {
        val favoriteTime = if (restaurant.favoriteTime != null) null else System.currentTimeMillis()
        database.restaurantQueries.insertOrUpdate(
            restaurant.slug,
            favoriteTime,
            restaurant.monitoredTime
        )
    }

    fun toggleMonitored(restaurant: Restaurant) {
        val monitoredTime =
            if (restaurant.monitoredTime != null) null else System.currentTimeMillis()
        database.restaurantQueries.insertOrUpdate(
            restaurant.slug,
            restaurant.favoriteTime,
            monitoredTime
        )
    }
}

private data class FavoritesAndMonitored(
    val favoritesQuery: Query<RestaurantDao>,
    val monitoredQuery: Query<RestaurantDao>,
)

private enum class RestaurantsPredicate {
    Monitored, Favorite
}

private const val LOCALE_HEBREW = "iw"
private const val WOLT_HEBREW = "he"
