package no.uio.ifi.in2000.weatheru.data.sunrise

import no.uio.ifi.in2000.weatheru.data.timezone.fetchTimeZone
import no.uio.ifi.in2000.weatheru.model.sunrise.SunriseProperties

class SunriseRepo {

    //  Return null is a proper timeZone cannot be gotten.
    //  Might instead use the default timezone from met instead in this instance in the future.
    suspend fun deserializeSunriseApi(latitude: Double, longitude: Double): SunriseProperties? {
        val timeZone = fetchTimeZone("QGJZY9D0RQCG", latitude, longitude) ?: return null
        val date = timeZone.formatted.substringBefore(" ")
        val apiOffset = convertGmtOffsetToApiOffset(timeZone.gmtOffset)

        return deserializeSunrise(latitude, longitude, date, apiOffset)
    }


    fun getSunrise(sunriseProperties: SunriseProperties?): String? {
        if (sunriseProperties == null) return null

        return sunriseProperties.properties.sunrise.time.substringAfter("T")
            .substringBefore("+").substringBefore("-")
    }

    fun getSunset(sunriseProperties: SunriseProperties?): String? {
        if (sunriseProperties == null) return null

        return sunriseProperties.properties.sunset.time.substringAfter("T")
            .substringBefore("+").substringBefore("-")
    }
}

fun convertGmtOffsetToApiOffset(gmtOffset: Int): String {
    val hours = gmtOffset / 3600
    return when {
        hours < -9 -> "${hours}:00"
        hours < 0 -> "-0${-hours}:00"
        hours < 10 -> "+0$hours:00"
        else -> "+$hours:00"
    }
}