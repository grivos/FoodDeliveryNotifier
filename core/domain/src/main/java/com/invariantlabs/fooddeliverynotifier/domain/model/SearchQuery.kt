package com.invariantlabs.fooddeliverynotifier.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchQuery(
    val query: String,
) : Parcelable
