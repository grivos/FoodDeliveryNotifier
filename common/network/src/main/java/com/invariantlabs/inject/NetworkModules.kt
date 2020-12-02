package com.invariantlabs.inject

import android.util.Log
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.invariantlabs.network.BuildConfig
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModules {
    fun injectFeature() = loadFeature
}

private val loadFeature by lazy {
    loadKoinModules(
        listOf(
            networkModule,
        )
    )
}

val networkModule = module {

    single<Interceptor> {
        LoggingInterceptor.Builder()
            .setLevel(if (BuildConfig.DEBUG) Level.NONE else Level.BASIC)
            .log(Log.INFO)
            .request("Request")
            .response("Response")
            .build()
    }

    single<CallAdapter.Factory> { RxJava2CallAdapterFactory.create() }

    single<Converter.Factory> { MoshiConverterFactory.create() }

    single {
        OkHttpClient.Builder()
            .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS))
            .addInterceptor(get<Interceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .client(get())
            .addConverterFactory(get())
            .addCallAdapterFactory(get())
    }
}
