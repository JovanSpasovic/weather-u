package no.uio.ifi.in2000.weatheru.data.forecast

import TimeZoneData
import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import no.uio.ifi.in2000.weatheru.model.forecast.LocationForecast
import no.uio.ifi.in2000.weatheru.util.NetworkClient


suspend fun deserializeForecast(latitude: Double, longitude: Double, timeZoneData: TimeZoneData?): LocationForecast? {
    return try {
        if (!areValidCoordinates(latitude, longitude)) return null

        // Utilize existing client from NetworkClient
        val response = NetworkClient.metClient.get("weatherapi/locationforecast/2.0/compact?lat=$latitude&lon=$longitude")
        if (response.status.isSuccess()) {
            val result = response.body<LocationForecast>()
            result.copy(timeZoneData = timeZoneData ?: TimeZoneData()) // Return the modified result with timezone data, filled with default values is something went wrong
        } else {
            Log.e("ForecastDatasource", "Failed to fetch forecast: HTTP ${response.status.value}")
            null
        }
    } catch (e: Exception) {
        Log.e("ForecastDatasource", "Error deserializing forecast: ${e.message}")
        null
    }
}

private fun areValidCoordinates(latitude: Double, longitude: Double): Boolean {
    if (latitude == 0.0 && longitude == 0.0) {  //  Have been a bug but does not seem to happen anymore
        Log.e("ForecastDatasource", "Invalid coordinates: Got 0.0, values were likely not found.")
        return false
    }
    if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
        Log.e("ForecastDatasource", "Coordinates out of range: Latitude: $latitude, Longitude: $longitude")
        return false
    }
    return true
}