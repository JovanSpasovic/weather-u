package no.uio.ifi.in2000.weatheru.ui.location

data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class LocationWithName(
    val name: String = "",
    val location: Location? = null
)