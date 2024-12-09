package no.uio.ifi.in2000.weatheru.ui.UIState

import no.uio.ifi.in2000.weatheru.model.nominatim.Address

data class ForecastUIState(
    val temp10Hours: List<Triple<String, Double, String>>? = null,
    val tempTomorrow24h: List<Triple<String, Double, String>>? = null,
    val currentChanceOfRain: Double? = null,
    val hourlyRainForNext10Hours: List<Double>? = null,
    val hourlyRainForNext24Hours: List<Double>? = null,
    val rainForNext7Days: List<List<Double>>? = null,
    val currentWindSpeed: Double? = null,
    val currentWindFromDirection: Double? = null,
    val hourlyWindSpeedAndDirectionForNext24Hours: List<Pair<Double, Double>>? = null,
    val currentSymbolCode: String? = null,
    val currentTemperature: Double? = null,
    val currentDate: String? = null,
    val currentTemperatureCode: String? = null,
    val temp7Hours: List<Triple<String, Double, String>>? = null,
    val maxtemperature7xdays: List<Double>? = null,
    val mintemperatures7xdays: List<Double>? = null,
    val getDailyDateAndCloudInfo: Map<String, String>? = null,
    val isThereAnyRainToday: Boolean? = null,
    val address: Address? = null,
    val country: String? = null,
    val displayName: String? = null,
    val sunrise: String? = null,
    val sunset: String? = null,
    val timeZone: String? = null,
    val description: String? = null,
    val locationPair: Pair<Double, Double>? = null
)




