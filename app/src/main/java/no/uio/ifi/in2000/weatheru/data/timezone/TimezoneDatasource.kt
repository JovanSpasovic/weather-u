package no.uio.ifi.in2000.weatheru.data.timezone

import TimeZoneData
import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import no.uio.ifi.in2000.weatheru.util.NetworkClient

suspend fun fetchTimeZone(apiKey: String, latitude: Double, longitude: Double): TimeZoneData? {
    return try {
        val response = NetworkClient.metClient.get("https://api.timezonedb.com/v2.1/get-time-zone") {
            parameter("key", apiKey)
            parameter("format", "json")
            parameter("by", "position")
            parameter("lat", latitude)
            parameter("lng", longitude)
        }
        if (response.status.isSuccess()) {
            response.body<TimeZoneData>()
        } else {
            Log.e("TimezoneDatasource","Failed to fetch time zone data: ${response.status.description}")
            null
        }
    } catch (e: Exception) {
        Log.e("TimezoneDatasource","Error fetching time zone data: ${e.message}")
        null
    }
}
