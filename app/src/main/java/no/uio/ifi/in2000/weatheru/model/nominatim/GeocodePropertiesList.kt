package no.uio.ifi.in2000.weatheru.model.nominatim



data class GeocodeProperties(
    val place_id: Long = 0,
    val licence: String = "",
    val lat: String = "",
    val lon: String = "",
    val addresstype: String = "",
    val display_name: String = "",
    val name: String = "",
    val address: GeocodingAddress = GeocodingAddress()
)

//  For location name that is not country, call GeocodingAddress-field with .name
data class GeocodingAddress (
    val municipality: String = "",
    val county: String = "",
    val country: String = ""
) {
    val name: String
        get() = when {
            municipality.isNotBlank() -> municipality
            county.isNotBlank() -> county
            else -> ""
        }
}
