package com.invariantlabs.fooddeliverynotifier.api

import com.invariantlabs.fooddeliverynotifier.data.dto.WoltSearchResultDto
import com.invariantlabs.fooddeliverynotifier.data.dto.WoltSlugResultDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WoltApi {
    @GET("v1/search?limit=50&sort=relevancy")
    fun getSearchResult(
        @Query("q") query: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Single<WoltSearchResultDto>

    @GET("v3/venues/slug/{slug}")
    fun getRestaurantSingle(@Path("slug") slug: String): Single<WoltSlugResultDto>

    @GET("v3/venues/slug/{slug}")
    suspend fun getRestaurant(@Path("slug") slug: String): WoltSlugResultDto
}
