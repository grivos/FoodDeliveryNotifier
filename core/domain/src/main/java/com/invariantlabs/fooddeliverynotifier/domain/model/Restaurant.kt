package com.invariantlabs.fooddeliverynotifier.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Restaurant(
    val name: String,
    val slug: String,
    val imageUrl: String,
    val deliveryStatus: DeliveryStatus,
    val favoriteTime: Long?,
    val publicUrl: String,
    val monitoredTime: Long?,
) : Parcelable

enum class DeliveryStatus {
    Online,
    Offline,
    NotInDeliveryHours,
}

val Restaurant.isFavorite: Boolean
    get() = favoriteTime != null

val Restaurant.isMonitored: Boolean
    get() = monitoredTime != null
