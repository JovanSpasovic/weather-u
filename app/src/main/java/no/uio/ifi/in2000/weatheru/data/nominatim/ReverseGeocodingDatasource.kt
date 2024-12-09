package no.uio.ifi.in2000.weatheru.data.nominatim

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import no.uio.ifi.in2000.weatheru.model.nominatim.ReverseGeocodeProperties
import no.uio.ifi.in2000.weatheru.util.NetworkClient



/**
 * Attempts to deserialize the data received from the Nominatim API.
 * If the data is successfully deserialized, a ReverseGeocodeProperties object is returned.
 * If the data cannot be deserialized successfully, null is returned.
 */
suspend fun deserializeReverseGeocoding(latitude: Double, longitude: Double): ReverseGeocodeProperties? {
    return try {
        // Check validity of coordinates
        if (!areValidCoordinates(latitude, longitude)) return null

        val url = "https://nominatim.openstreetmap.org/reverse?lat=${latitude}&lon=${longitude}&format=json&accept-language=en"
        // Utilize existing client from NetworkClient
        val response = NetworkClient.baseClient.get(url)
        if (response.status.isSuccess()) {
            response.body<ReverseGeocodeProperties>()
        } else {
            Log.e("deserializeReverseGeocoding", "Failed to fetch reverse geocoding: HTTP ${response.status.value}")
            null
        }

    } catch (e: Exception) {
        Log.e("deserializeReverseGeocoding", "Failed to deserialize reverse geocoding Nominatim data: ${e.message}", e)
        null
    }
}

/**
 * Helper function to validate the coordinates (latitude and longitude) that are sent in the request.
 * It checks if the latitude is within the range of -90.0 to 90.0 and if the longitude is within the range of -180.0 to 180.0.
 * It also checks if both the latitude and longitude are not 0.0, as this is considered an unlikely real-world location.
 * Returns true if the coordinates are valid, false otherwise.
 */
private fun areValidCoordinates(latitude: Double, longitude: Double): Boolean {
    if (latitude == 0.0 && longitude == 0.0) {  //  Have been a bug but does not seem to happen anymore
        Log.e("ReverseGeocodingDatasource", "Invalid coordinates: Got 0.0, values were likely not found.")
        return false
    }
    if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
        Log.e("ReverseGeocodingDatasource", "Coordinates out of range: Latitude: $latitude, Longitude: $longitude")
        return false
    }
    return true
}

