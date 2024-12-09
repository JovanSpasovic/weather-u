package no.uio.ifi.in2000.weatheru.data.alerts

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import no.uio.ifi.in2000.weatheru.model.alerts.CurrentAlerts
import no.uio.ifi.in2000.weatheru.model.alerts.Features


class AlertsRepo {

    suspend fun getAlertsDeserialized(): CurrentAlerts? {
        return try {
            deserializeAlerts()
        }
        catch (e: Exception) {
            Log.e("getAlertsDeserialized", "An unexpected error occurred", e) // General exception
            null // return null if an exception occurred
        }
    }


    /*Eksempel der "title" ikke gir konsekvent informasjon:
    "properties": {
        "area": "D2",
        "title": "Storm, D2, 17 mars 10:00 UTC til 18 mars 22:00 UTC. ",
    */

    //Get titles and descriptions of all current alerts
    //Return value is a list of pairs of strings
    //First string in pair is title of alert
    //Second string in pair is description of alert

    //  MUST take a NON-NULL parameter
     fun getCurrentEventAwarenessName(feature: Features): String {
        //Get alert eventAwarenessName as string
        return feature.properties.eventAwarenessName
    }

     fun getEvent(feature: Features): String {
        //Get alert event as string
        return feature.properties.event
    }


    fun getCurrentDescription(feature: Features): String {
        //Get alert description as string
        return feature.properties.description
    }


    fun getCurrentInstruction(feature: Features): String {
        //Get alert instruction as string
        return feature.properties.instruction
    }

    fun getCurrentConsequences(feature: Features): String {
        //Get alert consequences as string
        return feature.properties.consequences
    }


    fun getEventEndingTime(feature: Features) : String { //Does not exist for every alert
        //Get alert eventEndingTime as string

        val eventStartTime: String = feature.eventWhen.interval[0].substringBefore(("T"))

        val eventEndingTime: String = feature.eventWhen.interval[1].substringBefore("T")

        return "Gjelder fra $eventStartTime til $eventEndingTime"

    }

    fun getAreaDescription(feature: Features): String {

        //Get area description as string
        return feature.properties.area
    }

    fun getCurrentRiskMatrixColors(feature: Features): String {
        //Get risk matrix colors as string
        return feature.properties.riskMatrixColor
    }

    fun getLastChange(deserializedAlerts: CurrentAlerts): String {
        //Get alert lastChange as string
        return deserializedAlerts.lastChange
    }



    //  If no such alerts are found, the function returns null.
    fun getAlertIfDangerExistsInArea(deserializedAlerts: CurrentAlerts, latitude: Double, longitude: Double): Features? {

        //  Location can probably be found in multiple polygons, but as they share geographical locations, the data for weather should be similar.
        //  Because of this, only the first found polygon/multi-polygon will be returned

        val featuresList = deserializedAlerts.features

        featuresList.forEach { feature ->

            if (feature.geometry.type == "Polygon") {
                val polygonPoints: List<LatLng> = flattenPolygonCoordinates(feature.geometry.coordinates as List<List<List<Double>>>)
                if (PolyUtil.containsLocation(longitude, latitude, polygonPoints, false)) {
                    return feature
                }
            }

            else if (feature.geometry.type == "MultiPolygon") {
                feature.geometry.coordinates.forEach {polygon ->
                    val polygonPoints: List<LatLng> = flattenPolygonCoordinates(polygon as List<List<List<Double>>>)
                    if (PolyUtil.containsLocation(longitude, latitude, polygonPoints, false)) {
                        return feature
                    }

                }
            }
        }

        return null
    }

    // Function to flatten nested lists
    private fun flattenPolygonCoordinates(polygonList: List<List<List<Double>>>): List<LatLng> {
        return polygonList.flatten().map { LatLng(it[0], it[1]) }
    }

}

