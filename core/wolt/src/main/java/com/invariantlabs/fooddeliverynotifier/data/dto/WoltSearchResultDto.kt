package com.invariantlabs.fooddeliverynotifier.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WoltSearchResultDto(
    @Json(name = "results") val restaurants: List<WoltRestaurantWrapperDto>,
)

@JsonClass(generateAdapter = true)
data class WoltSlugResultDto(
    @Json(name = "results") val restaurants: List<WoltRestaurantDto>,
)

@JsonClass(generateAdapter = true)
data class WoltRestaurantWrapperDto(
    @Json(name = "value") val value: WoltRestaurantDto,
)

@JsonClass(generateAdapter = true)
data class WoltRestaurantDto(
    @Suppress("SpellCheckingInspection") @Json(name = "listimage") val imageUrl: String,
    @Json(name = "name") val name: List<WoltLanguageValueDto>,
    @Json(name = "online") val online: Boolean,
    @Json(name = "alive") val alive: Int,
    @Json(name = "slug") val slug: String,
    @Json(name = "delivery_specs") val deliverySpecs: WoltDeliverySpecsDto,
    @Json(name = "public_url") val publicUrl: String,
)

@JsonClass(generateAdapter = true)
data class WoltDeliverySpecsDto(
    @Json(name = "delivery_times") val deliveryTimes: WoltOpeningTimesWrapperDto,
)

@JsonClass(generateAdapter = true)
data class WoltOpeningTimesWrapperDto(
    @Json(name = "sunday") val sunday: List<WoltDateValueWrapperDto>,
    @Json(name = "monday") val monday: List<WoltDateValueWrapperDto>,
    @Json(name = "tuesday") val tuesday: List<WoltDateValueWrapperDto>,
    @Json(name = "wednesday") val wednesday: List<WoltDateValueWrapperDto>,
    @Json(name = "thursday") val thursday: List<WoltDateValueWrapperDto>,
    @Json(name = "friday") val friday: List<WoltDateValueWrapperDto>,
    @Json(name = "saturday") val saturday: List<WoltDateValueWrapperDto>,
)

@JsonClass(generateAdapter = true)
data class WoltLanguageValueDto(
    @Json(name = "lang") val language: String,
    @Json(name = "value") val value: String,
)

@JsonClass(generateAdapter = true)
data class WoltDateValueWrapperDto(
    @Json(name = "type") val type: String,
    @Json(name = "value") val value: WoltDateValuerDto,
)

@JsonClass(generateAdapter = true)
data class WoltDateValuerDto(
    @Json(name = "\$date") val time: Int,
)
