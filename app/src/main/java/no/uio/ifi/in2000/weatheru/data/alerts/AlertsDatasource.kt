package no.uio.ifi.in2000.weatheru.data.alerts


import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import no.uio.ifi.in2000.weatheru.model.alerts.CurrentAlerts
import no.uio.ifi.in2000.weatheru.util.NetworkClient

suspend fun deserializeAlerts(): CurrentAlerts? {
    return try {
        // Utilize existing client from NetworkClient
        val response = NetworkClient.metClient.get("weatherapi/metalerts/2.0/current.json")
        if (response.status.isSuccess()) {
            response.body<CurrentAlerts>()
        } else {
            Log.e("AlertsDatasource", "Failed to fetch alerts: HTTP ${response.status.value}")
            null
        }
    } catch (e: Exception) {
        Log.e("AlertsDatasource", "Failed to deserialize alerts: ${e.message}", e)
        null
    }
}
