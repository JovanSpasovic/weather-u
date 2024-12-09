package no.uio.ifi.in2000.weatheru.model.nominatim

data class ReverseGeocodeProperties(
    val place_id: Long = 0L,
    val licence: String = "",
    val osm_type: String = "",
    val osm_id: Long = 0L,
    val lat: String = "0.0",
    val lon: String = "0.0",
    val category: String = "",
    val type: String = "",
    val place_rank: Int = 0,
    val importance: Double = 0.0,
    val addresstype: String = "",
    val name: String = "",
    val display_name: String = "",
    val address: Address = Address(),
    val boundingbox: List<String> = emptyList()
)


data class Address(
    val amenity: String = "",
    val road: String = "",
    val neighbourhood: String = "",
    val suburb: String = "",
    val municipality: String = "",
    val city: String = "",
    val ISO3166_2_lvl4: String = "",
    val postcode: String = "",
    val country: String = "",
    val country_code: String = "",
    val residential: String = "",
    val village: String = "",
    val town: String = "",
    val city_district: String = "",
    val state: String = "",
    val state_district: String = ""
)
 {
    val name: Any
        get() = when {
            city.isNotBlank() -> city
            town.isNotBlank() -> town
            village.isNotBlank() -> village
            residential.isNotBlank() -> residential
            city_district.isNotBlank() -> city_district
            state.isNotBlank() -> state
            state_district.isNotBlank() -> state_district
            else -> ""
        }
}
