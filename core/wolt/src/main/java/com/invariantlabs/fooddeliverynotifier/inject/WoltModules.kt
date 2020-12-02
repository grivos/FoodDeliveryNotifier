package com.invariantlabs.fooddeliverynotifier.inject

import com.invariantlabs.fooddeliverynotifier.api.WoltApi
import com.invariantlabs.fooddeliverynotifier.service.WoltService
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit

object WoltModules {
    fun injectFeature() = loadFeature
}

private val loadFeature by lazy {
    loadKoinModules(
        listOf(
            networkModule,
        )
    )
}

val networkModule: Module = module {
    single {
        val retrofitBuilder: Retrofit.Builder = get()
        retrofitBuilder.baseUrl(API_ROOT)
            .build()
            .create(WoltApi::class.java)
    }
    single { WoltService(woltApi = get(), database = get(), locationStore = get()) }
}

private const val API_ROOT = "https://restaurant-api.wolt.com/"
