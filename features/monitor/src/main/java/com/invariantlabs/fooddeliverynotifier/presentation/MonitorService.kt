package com.invariantlabs.fooddeliverynotifier.presentation

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.invariantlabs.fooddeliverynotifier.domain.Constants
import com.invariantlabs.fooddeliverynotifier.domain.model.DeliveryStatus
import com.invariantlabs.fooddeliverynotifier.domain.model.Restaurant
import com.invariantlabs.fooddeliverynotifier.monitor.R
import com.invariantlabs.fooddeliverynotifier.service.WoltService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class MonitorService : Service() {

    private val woltService: WoltService by inject()
    private var monitoredRestaurants: List<Restaurant> = emptyList()

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createMonitoringChannel()
            createDeliveryAvailableChannel()
        }
        startForeground(1, createForegroundNotification())
        woltService.getMonitored()
            .subscribe { monitored ->
                this.monitoredRestaurants = monitored
                if (monitored.isEmpty()) {
                    stopSelf()
                }
            }
        CoroutineScope(Dispatchers.IO).launch {
            do {
                if (monitoredRestaurants.isEmpty()) {
                    return@launch
                }
                for (restaurant in monitoredRestaurants) {
                    val status = try {
                        woltService.getRestaurantStatus(restaurant.slug)
                    } catch (e: Exception) {
                        Timber.e(e, "Error fetching status")
                        null
                    }
                    if (status == DeliveryStatus.Online) {
                        showAvailabilityNotification(
                            restaurant.slug,
                            restaurant.name,
                            restaurant.publicUrl
                        )
                        woltService.toggleMonitored(restaurant)
                    }
                }
                delay(INTERVAL_TIME_MILLIS)
            } while (true)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createForegroundNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createMonitoringChannel()
            createDeliveryAvailableChannel()
        }
        val launchAppIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, launchAppIntent, 0)
        val titleText = applicationContext.getString(R.string.notification_title)
        return NotificationCompat.Builder(
            applicationContext,
            Constants.MONITORING_CHANNEL_ID
        )
            .setContentTitle(titleText)
            .setTicker(titleText)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun showAvailabilityNotification(
        slug: String,
        restaurantName: String,
        restaurantUrl: String
    ) {
        val contentText = applicationContext.getString(
            R.string.notification_available_content_pattern,
            restaurantName
        )
        val resultIntent = Intent(Intent.ACTION_VIEW).apply { data = restaurantUrl.toUri() }
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, resultIntent, 0)
        val defaultNotificationUri = Settings.System.DEFAULT_NOTIFICATION_URI
        val notification = NotificationCompat.Builder(
            applicationContext,
            Constants.DELIVERY_AVAILABLE_CHANNEL_ID
        )
            .setContentTitle(applicationContext.getString(R.string.notification_available_title))
            .setContentText(contentText)
            .setTicker(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(VIBRATE_DURATION, VIBRATE_DURATION))
            .setSound(defaultNotificationUri)
            .build()
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = slug.hashCode() * 2
        notificationManager.notify(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMonitoringChannel() {
        val name = applicationContext.getString(R.string.monitoring_channel_name)
        val descriptionText = applicationContext.getString(R.string.monitoring_channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(Constants.MONITORING_CHANNEL_ID, name, importance)
        channel.description = descriptionText
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDeliveryAvailableChannel() {
        val name = applicationContext.getString(R.string.restaurant_channel_name)
        val descriptionText = applicationContext.getString(R.string.restaurant_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(Constants.DELIVERY_AVAILABLE_CHANNEL_ID, name, importance)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
            .build()
        val defaultNotificationUri = Settings.System.DEFAULT_NOTIFICATION_URI
        channel.setSound(defaultNotificationUri, audioAttributes)
        channel.enableVibration(true)
        channel.description = descriptionText
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

private const val INTERVAL_TIME_MILLIS = 30L * 1000L
private const val VIBRATE_DURATION = 1000L
