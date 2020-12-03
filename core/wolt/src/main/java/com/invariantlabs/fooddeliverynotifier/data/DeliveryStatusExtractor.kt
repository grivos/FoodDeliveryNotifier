package com.invariantlabs.fooddeliverynotifier.data

import com.invariantlabs.fooddeliverynotifier.data.dto.WoltDateValueWrapperDto
import com.invariantlabs.fooddeliverynotifier.data.dto.WoltRestaurantDto
import com.invariantlabs.fooddeliverynotifier.domain.model.DeliveryStatus
import org.joda.time.DateTime
import org.joda.time.Interval

class DeliveryStatusExtractor(private val dto: WoltRestaurantDto) {

    fun extractStatus(): DeliveryStatus {
        val now = DateTime.now()
        val days = listOf(
            dto.deliverySpecs.deliveryTimes.monday,
            dto.deliverySpecs.deliveryTimes.tuesday,
            dto.deliverySpecs.deliveryTimes.wednesday,
            dto.deliverySpecs.deliveryTimes.thursday,
            dto.deliverySpecs.deliveryTimes.friday,
            dto.deliverySpecs.deliveryTimes.saturday,
            dto.deliverySpecs.deliveryTimes.sunday,
        )
        val todayIndex = now.dayOfWeek().get() - 1
        val todayOpenHours = days[todayIndex]
        val isInOpenHours = isInOpenHours(now, days, todayIndex, todayOpenHours)
        return if (isInOpenHours) {
            if (dto.online && dto.alive == 1) {
                DeliveryStatus.Online
            } else {
                DeliveryStatus.Offline
            }
        } else {
            DeliveryStatus.NotInDeliveryHours
        }
    }

    private fun isInOpenHours(
        now: DateTime,
        days: List<List<WoltDateValueWrapperDto>>,
        todayIndex: Int,
        todayOpenHours: List<WoltDateValueWrapperDto>
    ): Boolean {
        val todayOpeningTime = getOpeningTime(todayOpenHours)
        return if (todayOpeningTime != null) {
            val todayStart = now.withTimeAtStartOfDay().plusMillis(todayOpeningTime)
            if (todayStart.isAfterNow) {
                handleBeforeTodayStartTime(now, days, todayIndex)
            } else {
                handleAfterTodayStart(now, todayOpeningTime, todayStart, todayOpenHours)
            }
        } else {
            false
        }
    }

    private fun handleBeforeTodayStartTime(
        now: DateTime,
        days: List<List<WoltDateValueWrapperDto>>,
        todayIndex: Int,
    ): Boolean {
        val yesterdayIndex = if (todayIndex == 0) days.lastIndex else todayIndex - 1
        val yesterdayOpenHours = days[yesterdayIndex]
        val yesterdayOpeningTime = getOpeningTime(yesterdayOpenHours)
        return if (yesterdayOpeningTime != null) {
            val yesterdayCloseTimeMillis = getClosingTime(yesterdayOpenHours)
            if (yesterdayCloseTimeMillis < yesterdayOpeningTime) {
                val closeTime =
                    now.withTimeAtStartOfDay().plusMillis(yesterdayCloseTimeMillis)
                Interval(now.withTimeAtStartOfDay(), closeTime).contains(now)
            } else {
                false
            }
        } else {
            false
        }
    }

    private fun handleAfterTodayStart(
        now: DateTime,
        todayOpeningTime: Int,
        todayStart: DateTime,
        todayOpenHours: List<WoltDateValueWrapperDto>
    ): Boolean {
        val closeTimeMillis = getClosingTime(todayOpenHours)
        val todayClose = if (closeTimeMillis > todayOpeningTime) {
            now.withTimeAtStartOfDay().plusMillis(closeTimeMillis)
        } else {
            now.withTimeAtStartOfDay().plusMillis(closeTimeMillis).plusDays(1)
        }
        return Interval(todayStart, todayClose).contains(now)
    }

    private fun getOpeningTime(openHours: List<WoltDateValueWrapperDto>): Int? {
        return openHours.firstOrNull { it.type == KEY_OPEN }?.value?.time
    }

    private fun getClosingTime(openHours: List<WoltDateValueWrapperDto>): Int {
        return openHours.firstOrNull { it.type == KEY_CLOSE }?.value?.time ?: DEFAULT_CLOSE_TIME
    }
}

private const val KEY_OPEN = "open"
private const val KEY_CLOSE = "close"
private const val DEFAULT_CLOSE_TIME = 86400000
