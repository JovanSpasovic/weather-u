package no.uio.ifi.in2000.weatheru.data.sunrise



import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import no.uio.ifi.in2000.weatheru.model.sunrise.SunriseProperties
import no.uio.ifi.in2000.weatheru.util.NetworkClient.baseClient


suspend fun deserializeSunrise(latitude: Double, longitude: Double, date: String, apiOffset: String): SunriseProperties? {

    // Checking coordinate validity
    if (!areValidCoordinates(latitude, longitude)) return null

    return try {
        val url = "https://api.met.no/weatherapi/sunrise/3.0/sun?lat=$latitude&lon=$longitude&date=$date&offset=$apiOffset"
        val response = baseClient.get(url)
        if (response.status.isSuccess()) {
            response.body<SunriseProperties>()
        } else {
            Log.e("SunriseDatasource", "Failed to fetch sunrise: HTTP ${response.status.value}")
            null
        }

    } catch (e: Exception) {
        Log.e("SunriseDatasource", "Failed to deserialize sunrise data: ${e.message}")
        null
    }
}


private fun areValidCoordinates(latitude: Double, longitude: Double): Boolean {
    if (latitude == 0.0 && longitude == 0.0) {  //  Have been a bug but does not seem to happen anymore
        Log.e("SunriseDatasource", "Invalid coordinates: Got 0.0, values were likely not found.")
        return false
    }
    if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
        Log.e("SunriseDatasource", "Coordinates out of range: Latitude: $latitude, Longitude: $longitude")
        return false
    }
    return true
}

