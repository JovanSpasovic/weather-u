package no.uio.ifi.in2000.weatheru.data.nominatim

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import no.uio.ifi.in2000.weatheru.model.nominatim.GeocodeProperties
import no.uio.ifi.in2000.weatheru.util.NetworkClient


suspend fun deserializeGeocoding(locationName: String): List<GeocodeProperties>? {
    return try {
        if (!isValidLocationName(locationName)) return null

        val url = "https://nominatim.openstreetmap.org/search?q=$locationName&format=json&accept-language=en&limit=5"
        // Utilize existing client from NetworkClient
        val response = NetworkClient.baseClient.get(url)
        if (response.status.isSuccess()) {
            val responseBody = response.body<List<GeocodeProperties>>()
            //Log.e("deserializeGeocoding", "Response body: $responseBody")
            responseBody
        } else {
            Log.e("GeocodingDatasource", "Failed to fetch geocoding: HTTP ${response.status.value}")
            null
        }

    } catch (e: Exception) {
        Log.e("GeocodingDatasource", "Failed to deserialize Geocoding Nominatim data: ${e.message}", e)
        null
    }
}

private fun isValidLocationName(locationName: String): Boolean {
    if (locationName.isBlank()) {
        Log.e("GeocodingDatasource", "Invalid String input in deserialize function!")
        return false
    }
    return true
}
