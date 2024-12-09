package no.uio.ifi.in2000.weatheru.model.sunrise

import no.uio.ifi.in2000.weatheru.model.alerts.Geometry

data class SunriseProperties(
    val copyright: String,
    val licenseURL: String,
    val type: String,
    val geometry: Geometry,
    val `when`: Interval,
    val properties: Properties
)

data class Interval(
    val interval: List<String>
)

data class Properties(
    val body: String,
    val sunrise: Sunrise,
    val sunset: Sunset,
    val solarnoon: Solarnoon,
    val solarmidnight: Solarmidnight
)

data class Sunrise(
    val time: String,
    val azimuth: Double
)

data class Sunset(
    val time: String,
    val azimuth: Double
)

data class Solarnoon(
    val time: String,
    val disc_centre_elevation: Double,
    val visible: Boolean
)

data class Solarmidnight(
    val time: String,
    val disc_centre_elevation: Double,
    val visible: Boolean
)