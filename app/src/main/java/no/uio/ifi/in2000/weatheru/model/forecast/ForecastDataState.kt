package no.uio.ifi.in2000.weatheru.model.forecast

import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey
import no.uio.ifi.in2000.weatheru.model.nominatim.Address
import no.uio.ifi.in2000.weatheru.ui.location.Location

@Entity(tableName = "forecast_states")  // Camelcase is not convention for databases
data class ForecastDataState(
    @PrimaryKey(autoGenerate = false) val id: Int, // 0 used for current location, 1 for other locations
    val temp10Hours : List<Triple<String, Double, String>>? = null,
    val tempTomorrow24h: List<Triple<String, Double, String>>? = null,
    val currentChanceOfRain: Double? = null,
    val hourlyRainForNext10Hours: List<Double>? = null,
    val hourlyRainForNext24Hours: List<Double>? = null,
    val rainForNext7Days: List<List<Double>>? = null,
    val currentWindSpeed : Double? = null,
    val currentWindFromDirection : Double? = null,
    val hourlyWindSpeedAndDirectionForNext24Hours : List<Pair<Double, Double>>? = null,
    val currentSymbolCode : String? = null,

    // Main Cards
    val currentTemperature: Double? = null,
    val currentDate: String? = null,
    val currentTemperatureCode: String? = null,
    val temp7Hours: List<Triple<String, Double, String>>? = null,
    val maxTemperature5Days: List<Double>? = null,
    val minTemperatures5Days: List<Double>? = null,
    val dailyDateAndCloudInfo: Map<String, String>? = null,
    val isThereAnyRainToday: Boolean? = null,

    // Last update time
    val lastChange: String = "",

    val locationForecast: LocationForecast? = null,
    val locationPair: Location? =  null,

    //  Reverse geocoding
    val address: Address? = null,
    val country: String? = null,
    val displayName: String? = null,

    val sunrise: String? = null,
    val sunset: String? = null,
    val timeZone : String? = null,
    val description: String? = null,

    )


@DatabaseView("""
    SELECT id, 
           temp10Hours, 
           tempTomorrow24h,
           currentChanceOfRain, 
           hourlyRainForNext10Hours, 
           hourlyRainForNext24Hours, 
           rainForNext7Days, 
           currentWindSpeed, 
           currentWindFromDirection, 
           hourlyWindSpeedAndDirectionForNext24Hours, 
           currentSymbolCode, 
           currentTemperature, 
           currentDate, 
           currentTemperatureCode, 
           temp7Hours, 
           maxTemperature5Days, 
           minTemperatures5Days,
           dailyDateAndCloudInfo,
           isThereAnyRainToday,
           lastChange,
           address,
           country,
           displayName,
           sunrise,
           sunset,
           timeZone,
           displayName, 
           description
    FROM forecast_states
""")
data class ForecastStateView(
    @PrimaryKey(autoGenerate = false) val id: Int = 0,
    val temp10Hours : List<Triple<String, Double, String>>? = null,
    val tempTomorrow24h: List<Triple<String, Double, String>>? = null,
    val currentChanceOfRain: Double? = null,
    val hourlyRainForNext10Hours: List<Double>? = null,
    val hourlyRainForNext24Hours: List<Double>? = null,
    val rainForNext7Days: List<List<Double>>? = null,
    val currentWindSpeed : Double? = null,
    val currentWindFromDirection : Double? = null,
    val hourlyWindSpeedAndDirectionForNext24Hours : List<Pair<Double, Double>>? = null,
    val currentSymbolCode : String? = null,

    // Main Cards
    val currentTemperature: Double? = null,
    val currentDate: String? = null,
    val currentTemperatureCode: String? = null,
    val temp7Hours: List<Triple<String, Double, String>>? = null,
    val maxTemperature5Days: List<Double>? = null,
    val minTemperatures5Days: List<Double>? = null,
    val dailyDateAndCloudInfo: Map<String, String>? = null,
    val isThereAnyRainToday: Boolean? = null,

    // Last update time
    val lastChange: String = "",

    val locationPair: Location? =  null,

    //  Reverse geocoding
    val address: Address? = null,
    val country: String? = null,
    val displayName: String? = null,

    val sunrise: String? = null,
    val sunset: String? = null,
    val timeZone : String? = null,
    val description: String? = null,



)

