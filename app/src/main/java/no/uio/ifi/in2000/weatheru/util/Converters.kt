package no.uio.ifi.in2000.weatheru.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import no.uio.ifi.in2000.weatheru.model.alerts.Features
import no.uio.ifi.in2000.weatheru.model.alerts.Geometry
import no.uio.ifi.in2000.weatheru.model.forecast.LocationForecast
import no.uio.ifi.in2000.weatheru.model.nominatim.Address
import no.uio.ifi.in2000.weatheru.ui.location.Location


class Converters {

    private val gson = Gson()

    //  Double
    @TypeConverter
    fun fromDoubleList(value: List<Double>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun fromDoubleListList(value: List<List<Double>>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDoubleList(value: String): List<Double> {
        val listType = object : TypeToken<List<Double>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toDoubleListList(value: String): List<List<Double>> {
        val listType = object : TypeToken<List<List<Double>>>() {}.type
        return gson.fromJson(value, listType)
    }

    //  String
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<Features>>() {}.type
        return gson.fromJson(value, listType)
    }

    //  Features
    @TypeConverter
    fun fromFeaturesList(value: List<Features>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toFeaturesList(value: String): List<Features> {
        val listType = object : TypeToken<List<Features>>() {}.type
        return gson.fromJson(value, listType)
    }

    //  Geometry
    @TypeConverter
    fun fromGeometry(value: Geometry): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGeometry(value: String): Geometry {
        return gson.fromJson(value, Geometry::class.java)
    }

    //  Triple list
    @TypeConverter
    fun fromTripleList(value: List<Triple<String, Double, String>>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTripleList(value: String): List<Triple<String, Double, String>> {
        val listType = object : TypeToken<List<Triple<String, Double, String>>>() {}.type
        return gson.fromJson(value, listType)
    }

    //  hourlyWindSpeedAndDirectionForNext24Hours
    @TypeConverter
    fun fromWindSpeedAndDirectionList(value: List<Pair<Double, Double>>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWindSpeedAndDirectionList(value: String): List<Pair<Double, Double>> {
        val listType = object : TypeToken<List<Pair<Double, Double>>>() {}.type
        return gson.fromJson(value, listType)
    }

    //  coordinate pair
    @TypeConverter
    fun fromCoordinatePair(value: Location): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCoordinatePair(value: String): Location {
        val type = object : TypeToken<Location>() {}.type
        return gson.fromJson(value, type)
    }

    //  dailyDateAndCloudInfo
    @TypeConverter
    fun fromDateAndCloudInfo(value: Map<String, String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDateAndCloudInfo(value: String): Map<String, String> {
        val listType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, listType)
    }

    //  locationForecast
    @TypeConverter
    fun fromLocationForecast(value: LocationForecast): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLocationForecast(value: String): LocationForecast {
        val listType = object : TypeToken<LocationForecast>() {}.type
        return gson.fromJson(value, listType)
    }

    //  Pair of strings
    @TypeConverter
    fun fromPairStrings(value: Pair<String, String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPairStrings(value: String): Pair<String, String> {
        val listType = object : TypeToken<Pair<String, String>>() {}.type
        return gson.fromJson(value, listType)
    }

    //  Pair of strings
    @TypeConverter
    fun fromAddress(value: Address): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAddress(value: String): Address {
        val type = object : TypeToken<Address>() {}.type
        return gson.fromJson(value, type)
    }





}