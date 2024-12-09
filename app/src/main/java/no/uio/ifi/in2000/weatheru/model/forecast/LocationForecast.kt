package no.uio.ifi.in2000.weatheru.model.forecast

import TimeZoneData

data class LocationForecast(
    val properties: ForecastProperties,
    val timeZoneData: TimeZoneData
)



data class ForecastProperties(
    val meta: Meta,
    val timeseries: List<Timeseries>
)

data class Meta(
    val updated_at: String,
    val units: Units
)

data class Units(
    val air_pressure_at_sea_level: String,
    val air_temperature: String,
    val cloud_area_fraction: String,
    val precipitation_amount: String,
    val relative_humidity: String,
    val wind_from_direction: String,
    val wind_speed: String
)

data class Timeseries(
    val time: String,
    val data: Data
)

data class Data(
    val instant: Instant,
    val next_12_hours: Next12Hours,
    val next_1_hours: Next1Hours,
    val next_6_hours: Next6Hours
)

data class Next12Hours(
    val summary: Summary,
    val details: Details
)

data class Next1Hours(
    val summary: Summary,
    val details: Details
)

data class Next6Hours(
    val summary: Summary,
    val details: Details
)

data class Summary(
    val symbol_code: String
)


data class Instant(
    val details: Details
)


data class Details(
    val air_pressure_at_sea_level: Double,
    val air_temperature: Double,
    val cloud_area_fraction: Double,
    val relative_humidity: Double,
    val wind_from_direction: Double,
    val wind_speed: Double,
    val precipitation_amount: Double
)