package no.uio.ifi.in2000.weatheru.model.alerts

import com.google.gson.annotations.SerializedName


data class CurrentAlerts(
    val features: List<Features>,
    val lang: String,
    val lastChange: String,
    val type: String
)

data class Features(
    val geometry: Geometry,
    val properties: AlertProperties,
    val type: String,
    @SerializedName("when")
    val eventWhen: When
)

data class When(
    val interval: List<String>
)

data class Geometry(
    var coordinates: List<Any>, // Use a non-nullable list as the initial type
    val type: String
) {
    init {
        coordinates = if (type == "Polygon") {
            listOf<List<List<Double>>>()
        } else {
            listOf<List<List<List<Double>>>>()
        }
    }
}



data class AlertProperties(
    val altitude_above_sea_level: Int,
    val area: String,
    val awarenessResponse: String,
    val awarenessSeriousness: String,
    val awareness_level: String,
    val awareness_type: String,
    val ceiling_above_sea_level: Int,
    val certainty: String,
    val consequences: String,
    val contact: String,
    val county: List<String>,
    val description: String,
    val event: String,
    val eventAwarenessName: String,
    val eventEndingTime: String?,
    val geographicDomain: String,
    val id: String,
    val instruction: String,
    val resources: List<Resources>,
    val riskMatrixColor: String,
    val severity: String,
    val status: String,
    val title: String,
    val triggerLevel: String,
    val type: String,
    val web: String
)

data class Resources(
    val description: String,
    val mimeType: String,
    val uri: String
)