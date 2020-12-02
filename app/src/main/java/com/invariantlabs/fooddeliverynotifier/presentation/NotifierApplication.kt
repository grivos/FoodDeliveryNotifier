package com.invariantlabs.fooddeliverynotifier.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.grivos.spanomatic.SpanomaticInterceptor
import com.invariantlabs.fooddeliverynotifier.BuildConfig
import com.invariantlabs.fooddeliverynotifier.inject.*
import com.invariantlabs.fooddeliverynotifier.service.WoltService
import com.invariantlabs.inject.NetworkModules
import io.github.inflationx.viewpump.ViewPump
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import kotlin.random.Random

@Suppress("unused")
class NotifierApplication : Application() {

    private val woltService: WoltService by inject()

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        initKoin()
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    SpanomaticInterceptor()
                )
                .build()
        )
        if (BuildConfig.DEBUG) {
            val mode = if (Random.nextBoolean()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }
        JodaTimeAndroid.init(this)
        woltService.getMonitored()
            .subscribe { monitored ->
                if (monitored.isNotEmpty()) {
                    val intent = Intent(this, MonitorService::class.java)
                    startService(intent)
                }
            }
    }

    private fun initKoin() {
        startKoin { androidContext(this@NotifierApplication) }
        AppModules.injectFeature()
        RestaurantsModules.injectFeature()
        NetworkModules.injectFeature()
        WoltModules.injectFeature()
        DomainModules.injectFeature()
        MonitorModules.injectFeature()
    }
}
