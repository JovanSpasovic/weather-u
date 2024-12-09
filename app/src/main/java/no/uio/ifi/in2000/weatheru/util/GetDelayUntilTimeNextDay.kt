package no.uio.ifi.in2000.weatheru.util

import java.util.Calendar
import java.util.TimeZone


fun getDelayUntilTimeNextDay(hourOfDay: Int, minute: Int, timeZoneId: String): Long {
    val timeZone = TimeZone.getTimeZone(timeZoneId)

    val now = Calendar.getInstance(timeZone)
    val targetTime = Calendar.getInstance(timeZone)

    // Set the target time to the specified hour, minute, and second.
    targetTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
    targetTime.set(Calendar.MINUTE, minute)
    targetTime.set(Calendar.SECOND, 0)
    targetTime.set(Calendar.MILLISECOND, 0)

    // Check if the target time is already past for today. If so, set the target to the next day.
    if (targetTime.before(now)) {
        targetTime.add(Calendar.DAY_OF_MONTH, 1)
    }

    // Calculate the delay in milliseconds.
    val delay = targetTime.timeInMillis - now.timeInMillis

    return delay
}
