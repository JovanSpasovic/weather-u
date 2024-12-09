package no.uio.ifi.in2000.weatheru.data.nominatim

import no.uio.ifi.in2000.weatheru.model.nominatim.Address
import no.uio.ifi.in2000.weatheru.model.nominatim.GeocodeProperties
import no.uio.ifi.in2000.weatheru.model.nominatim.ReverseGeocodeProperties
import no.uio.ifi.in2000.weatheru.ui.location.Location
import no.uio.ifi.in2000.weatheru.ui.location.LocationWithName

class NominatimRepo {

    suspend fun fetchGeocoding(search : String): List<GeocodeProperties>? {
        return deserializeGeocoding(search)
    }



    /**
     * This function returns a list of LocationWithName objects.
     * Each LocationWithName object contains the name of the location and its coordinates (longitude and latitude).
     * The name is used to display the location in the UI.
     * The coordinates are used to fetch and run a weather forecast for the respective location.
     */
    fun getLocationWithNameList(deserializedGeocodeList: List<GeocodeProperties>?): List<LocationWithName>? {
        if(deserializedGeocodeList == null) return null

        val resultList = deserializedGeocodeList.map { property ->
            val latitude = property.lat
            val longitude = property.lon
            val displayName = property.display_name

            val beforeFirstComma = displayName.substringBefore(",")
            val beforeSecondComma = displayName.substringAfter(",").substringBefore(",")
            val country = displayName.substringAfterLast(",")


            val locationName = if (beforeFirstComma != country) {
                if (beforeSecondComma != country) {
                    "$beforeFirstComma, $beforeSecondComma, $country"
                } else {
                    "$beforeFirstComma, $country"
                }
            } else {
                country
            }

            LocationWithName(locationName, Location(latitude.toDouble(), longitude.toDouble()))
        }

        return resultList
    }



    suspend fun fetchReverseGeocoding(latitude: Double, longitude: Double): ReverseGeocodeProperties? {
        return deserializeReverseGeocoding(latitude, longitude)
    }

    fun getDisplayName(reverseGeocodeProperties: ReverseGeocodeProperties?): String? {
        return reverseGeocodeProperties?.display_name
    }


    fun getAddressAndCountry(reverseGeocodeProperties: ReverseGeocodeProperties?): Pair<Address?, String> {
        return Pair(reverseGeocodeProperties?.address, reverseGeocodeProperties?.address?.country ?: "")
    }

}