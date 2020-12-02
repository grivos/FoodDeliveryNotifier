package com.invariantlabs.fooddeliverynotifier.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoredLocation(
    val address: String,
    val lat: Double,
    val lng: Double,
) : Parcelable
