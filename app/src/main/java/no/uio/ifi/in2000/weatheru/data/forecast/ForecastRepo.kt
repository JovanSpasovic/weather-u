package no.uio.ifi.in2000.weatheru.data.forecast

import no.uio.ifi.in2000.weatheru.data.timezone.fetchTimeZone
import no.uio.ifi.in2000.weatheru.model.descriptions.TemperatureDescriptions
import no.uio.ifi.in2000.weatheru.model.descriptions.WeatherDescriptions
import no.uio.ifi.in2000.weatheru.model.forecast.LocationForecast
import no.uio.ifi.in2000.weatheru.util.convertDateToLocalTimeInHours
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale



/**
 * Class responsible for fetching and processing forecast data.
 */
class ForecastRepo {

    /**
     * To obtain the local time for a specific location, the 'gmtOffset' field from 'timeZoneData' is utilized.
     * This is combined with the 'convertDateToLocalTime()' function to convert a given date to the local time in whole hours.
     */
    suspend fun getForecastDeserialized(latitude: Double, longitude: Double): LocationForecast? {
        val timeZoneData = fetchTimeZone(APIKEY, latitude, longitude)
        return deserializeForecast(latitude, longitude, timeZoneData)
    }

    fun getLastChange(dataDeserialized: LocationForecast): String {
        //  Gets string of date of last API update
        return dataDeserialized.properties.timeseries[0].time   //  Technically not update time, but works as it for our purposes
    }

    fun getTimeZone(dataDeserialized: LocationForecast): String {
        return dataDeserialized.timeZoneData.zoneName
    }


    // function getCityCountry gets  deserializedData : LocationForecast returns the city and country
    // getFromLocation is deprecated in API 29, so maybe use google play api instead of geocoder
    // This function is used to get the city and country from the latitude and longitude of the user's location using the Geocoder class.
    // The Geocoder class is used to handle geocoding and reverse geocoding, which is the process of converting between geographic coordinates and human-readable addresses.
    // The function takes the latitude and longitude as input and returns the city and country as a string.


    //Get current date, used this api java.time package in Kotlin

    fun getCurrentDate(deserializedData: LocationForecast): String {
        val timeseriesList = deserializedData.properties.timeseries

        // List of Timeseries objects grouped by date
        val timeseriesListGroupedByDays = timeseriesList.groupBy {
            it.time.substringBefore("T")
        }

        // Get the current date
        val currentDate = timeseriesListGroupedByDays.keys.first()

        // Parse the date string to LocalDate
        val date = LocalDate.parse(currentDate)

        // Format the date to "1.mars 2024"
        val formatter = DateTimeFormatter.ofPattern("d.MMMM yyyy", Locale("no"))
        val formattedDate = date.format(formatter)

        return formattedDate
    }

    //Temperature
    fun getCurrentTemperature(deserializedData : LocationForecast): Double {
        return deserializedData.properties.timeseries[0].data.instant.details.air_temperature
    }

    fun getTemperatureUnit(deserializedData : LocationForecast): String {
        return deserializedData.properties.meta.units.air_temperature
    }


    fun getHourlyTemperatureAndCloudsFor10Hours(deserializedData: LocationForecast): List<Triple<String, Double, String>> {
        val timeseriesList = deserializedData.properties.timeseries.take(10)
        val gmtOffset = deserializedData.timeZoneData.gmtOffset

        return timeseriesList.map {
            Triple(
                convertDateToLocalTimeInHours(it.time, gmtOffset),
                it.data.instant.details.air_temperature,
                it.data.next_1_hours.summary.symbol_code
            )
        }
    }

    //Is this correct??



    fun getHourlyTemperatureAndCloudsForTomorrow24Hours(deserializedData: LocationForecast): List<Triple<String, Double, String>> {
        val timeseriesList = deserializedData.properties.timeseries
        val gmtOffset = deserializedData.timeZoneData.gmtOffset

        // Convert offset from seconds to hours and create ZoneOffset instance
        val gmtOffsetHours = gmtOffset / 3600
        val myOffset = ZoneOffset.ofHours(gmtOffsetHours)

        // Get the current date and time using your own offset
        val currentDateTime = LocalDateTime.now(myOffset)

        // Get the start of the next day using your own offset
        val startOfNextDay = currentDateTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)

        // Filter the timeseriesList to only include entries where the time is after the start of the next day
        val filteredTimeseriesList = timeseriesList.dropWhile {
            LocalDateTime.parse(it.time, DateTimeFormatter.ISO_DATE_TIME).atOffset(ZoneOffset.UTC).isBefore(startOfNextDay.atOffset(myOffset))
        }.take(24)

        return filteredTimeseriesList.map {
            Triple(
                convertDateToLocalTimeInHours(it.time, gmtOffset),
                it.data.instant.details.air_temperature,
                it.data.next_1_hours.summary.symbol_code
            )
        }
    }

    fun getHourlyTemperatureFor7Hours(deserializedData: LocationForecast): List<Triple<String, Double, String>> {
        val timeseriesList = deserializedData.properties.timeseries.take(7)
        val gmtOffset = deserializedData.timeZoneData.gmtOffset  //  get offset in whole hours

        return timeseriesList.map {
            Triple(
                convertDateToLocalTimeInHours(it.time, gmtOffset),
                it.data.instant.details.air_temperature,
                it.data.next_1_hours.summary.symbol_code
            )
        }
    }


    fun getCurrentSymbolCode(deserializedData : LocationForecast): String {
        return deserializedData.properties.timeseries[0].data.next_1_hours.summary.symbol_code
    }

    //  not in use
    fun getDailyMaxTemperaturesForNext7Days(deserializedData : LocationForecast): List<Double> {

        val timeseriesList = deserializedData.properties.timeseries

        //  List of Timeseries objects grouped by date
        val timeseriesListGroupedByDays = timeseriesList.groupBy {
            it.time.substringBefore("T")
        }

        // List of the max temperature for each day for the next 7 days
        val maxTemperatures = timeseriesListGroupedByDays.map { (_, timeseriesList) ->
            timeseriesList.maxOf { it.data.instant.details.air_temperature }    // Finds the max temperature in each group
        }.take(7)

        return maxTemperatures

    }


    //Clouds
    fun getCurrentCloudAreaFraction(deserializedData : LocationForecast): Double {

        return deserializedData.properties.timeseries[0].data.instant.details.cloud_area_fraction

    }

    fun getCloudAreaFractionUnit(deserializedData : LocationForecast): String {
        return deserializedData.properties.meta.units.cloud_area_fraction
    }

    // Function to calculate the current chance of rain

    fun getCurrentPrecipitation(deserializedData: LocationForecast): Double {

        val currentData = deserializedData.properties.timeseries.firstOrNull()?.data?.next_1_hours?.details
        return currentData?.precipitation_amount ?: 0.0
    }

    fun getRainUnit(deserializedData : LocationForecast): String {

        return deserializedData.properties.meta.units.precipitation_amount
    }


    //RAIN

    fun isThereAnyRainToday(deserializedData: LocationForecast): Boolean {


        val timeseriesList = deserializedData.properties.timeseries

        val today = timeseriesList.first().time.substringBefore("T")

        //  Could simply get the 24 first elements from timeseriesList for more efficient code
        val todayTimeseries = timeseriesList.filter { it.time.substringBefore("T") == today }

        return todayTimeseries.any {
            it.data.next_1_hours.details.precipitation_amount > 0.0
        }
    }


    fun getHourlyRainForNext24Hours(deserializedData : LocationForecast): List<Double> {

        val timeseriesList = deserializedData.properties.timeseries.take(24)
        return timeseriesList.map { it.data.next_1_hours.details.precipitation_amount ?: 0.0 }

    }

    fun getHourlyRainForNext10Hours(deserializedData : LocationForecast): List<Double> {

        val timeseriesList = deserializedData.properties.timeseries.take(10)
        return timeseriesList.map { it.data.next_1_hours.details.precipitation_amount ?: 0.0 }

    }

    fun getRainForNext7Days(deserializedData : LocationForecast): List<List<Double>> {

        val timeseriesList = deserializedData.properties.timeseries

        // Group timeseries by date
        val timeseriesListGroupedByDays = timeseriesList.groupBy {
            it.time.substringBefore("T")
        }

        // Calculate average precipitation for each day for the next 7 days
        val averagePrecipitation = timeseriesListGroupedByDays.mapNotNull { (_, timeseriesList) ->
            timeseriesList.mapNotNull {
                it.data.next_1_hours?.details?.precipitation_amount // Use mapNotNull to filter out null values
            }
        }.take(7)

        return averagePrecipitation
    }


    //Wind
    fun getCurrentWindSpeed(deserializedData : LocationForecast): Double {

        return deserializedData.properties.timeseries[0].data.instant.details.wind_speed
    }

    fun getCurrentWindFromDirection(deserializedData : LocationForecast): Double {

        return deserializedData.properties.timeseries[0].data.instant.details.wind_from_direction
    }


    fun getHourlyWindSpeedAndDirectionForNext24Hours(deserializedData : LocationForecast): List<Pair<Double, Double>> {

        val timeseriesList = deserializedData.properties.timeseries.take(24)

        //  List of pairs of doubles which represent wind speed and wind from direction, respectively
        val windSpeedAndDirectionList: List<Pair<Double, Double>> = timeseriesList.map {
            Pair(it.data.instant.details.wind_speed, it.data.instant.details.wind_from_direction)
        }

        return windSpeedAndDirectionList
    }


    //Function to get the symbol code for the current weather
    fun getCurrentTemperaturelCode(deserializedData: LocationForecast): String {
        // Get the first timeseries object
        val timeseries = deserializedData.properties.timeseries.firstOrNull()

        // Get the symbol code from the next_1_hours section
        val symbolCode = timeseries?.data?.next_1_hours?.summary?.symbol_code

        // Check if symbolCode is not null
        if (symbolCode != null) {
            // Format the symbol code to make it more readable
            val formattedSymbolCode = symbolCode.replace("_", " ").split(" ").joinToString(" ") { it ->
                it.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                } }
            return formattedSymbolCode
        } else {
            throw Exception("Could not retrieve symbol code")
        }


    }




    fun getDailyMaxTemperaturesForNext7xDays(deserializedData : LocationForecast): List<Double> {

        val timeseriesList = deserializedData.properties.timeseries

        //  List of Timeseries objects grouped by date
        val timeseriesListGroupedByDays = timeseriesList.groupBy {
            it.time.substringBefore("T")
        }

        // List of the max temperature for each day for the next 7 days
        val maxTemperatures = timeseriesListGroupedByDays.map { (_, timeseriesList) ->
            timeseriesList.maxOf { it.data.instant.details.air_temperature }    // Finds the max temperature in each group
        }

        return maxTemperatures.take(7)

    }


    fun getDailyMinTemperaturesForNext7xDays(deserializedData : LocationForecast): List<Double> {

        val timeseriesList = deserializedData.properties.timeseries

        //  List of Timeseries objects grouped by date
        val timeseriesListGroupedByDays = timeseriesList.groupBy {
            it.time.substringBefore("T")
        }

        // List of the max temperature for each day for the next 7 days
        val minTemperatures = timeseriesListGroupedByDays.map { (_, timeseriesList) ->
            timeseriesList.minOf { it.data.instant.details.air_temperature }    // Finds the max temperature for each date/day and replaces that value into the list
        }

        return minTemperatures.take(7)
    }


    /**
     * The 'cloudInfoForDay' variable represents a list of all the cloud information for a specific day's forecast.
     * By identifying the most frequent element in this list, the predominant cloud condition for that day is determined.
     * If the parsed date is today, the symbol code for the current hour (next_1_hours) is retrieved.
     * Otherwise, the most frequent symbol code for that day (next_12_hours) is retrieved.
     */
    fun getDailyDateAndCloudInfo(deserializedData: LocationForecast): Map<String, String> {
        val timeseriesList = deserializedData.properties.timeseries

        val timeseriesListGroupedByDays = timeseriesList.groupBy {
            it.time.substringBefore("T")
        }

        val dailyDateAndCloudInfo = mutableMapOf<String, String>()

        val currentDate = LocalDate.now()

        val currentSymbolCode = getCurrentSymbolCode(deserializedData)

        timeseriesListGroupedByDays.entries.take(7).forEach { (date, timeseriesList) ->
            try {
                val parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                val parseDay = parsedDate.dayOfWeek.name

                val formattedDate =
                    when (parseDay) {
                        "MONDAY" -> {
                            "Man"
                        }
                        "TUESDAY" -> {
                            "Tir"
                        }
                        "WEDNESDAY" -> {
                            "Ons"
                        }
                        "THURSDAY" -> {
                            "Tor"
                        }
                        "FRIDAY" -> {
                            "Fre"
                        }
                        "SATURDAY" -> {
                            "Lør"
                        }
                        else -> {
                            "Søn"
                        }
                    }

                val cloudInfoForDay = if (parsedDate == currentDate) {
                    listOf(currentSymbolCode)
                } else {
                    timeseriesList.mapNotNull {
                        it.data.next_6_hours?.summary?.symbol_code
                    }
                }

                val mostFrequentCloudInfo =
                    cloudInfoForDay.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: ""

                dailyDateAndCloudInfo[formattedDate] = mostFrequentCloudInfo
            } catch (e: DateTimeParseException) {
                e.printStackTrace()
            }
        }

        return dailyDateAndCloudInfo
    }


    companion object {
        const val APIKEY = "QGJZY9D0RQCG"  // Your actual API key
        // Other companion object members
    }


    //        "some_rain" to "Det er sjanse for regn idag. ",
    //        "rain" to "Idag blir det regn. Det kan være lurt å ta med seg en paraply! ",
    //        "hot" to "Det blir varmt idag, husk å drikke nok vann! ",
    //        "chilly" to "Det er kjølig idag, vi anbefaler å ta på en ekstra genser! ",
    //        "freezing" to "Det blir minusgrader idag, kle deg godt! ",
    //        "snow" to "Idag kan det bli snøvær. ",

    fun getWeatherDescription(deserializedData: LocationForecast): String {
        // Extract timeseries data
        val timeseriesList = deserializedData.properties.timeseries.take(24)
        val today = timeseriesList.first().time.substringBefore("T")
        val todayTimeseries = timeseriesList.filter { it.time.substringBefore("T") == today }

        // Compute average and categorize temperature
        val temperatures = timeseriesList.map { it.data.instant.details.air_temperature }
        val averageTemperature = temperatures.average()
        val maxTemperature = temperatures.maxOrNull()
        val minTemperature = temperatures.minOrNull()

        // Determine precipitation conditions
        val hasRain = todayTimeseries.any {
            (it.data.next_6_hours.details.precipitation_amount ?: 0.0) > 0.0
        }
        val isHeavyRain = todayTimeseries.any {
            (it.data.next_6_hours.details.precipitation_amount ?: 0.0) > 2.0
        }

        // Set weather and temperature descriptions based on conditions
        val weatherDescription = when {
            isHeavyRain -> WeatherDescriptions.weatherDescriptions["rain"] ?: "Rain"
            hasRain -> WeatherDescriptions.weatherDescriptions["some_rain"] ?: "Some Rain"
            else -> WeatherDescriptions.weatherDescriptions["no_rain"] ?: "No Rain"
        }

        val temperatureDescription = when {
            averageTemperature < 0 -> TemperatureDescriptions.temperatureDescriptions["freezing"] ?: "Freezing"
            averageTemperature < 10 -> TemperatureDescriptions.temperatureDescriptions["chilly"] ?: "Chilly"
            averageTemperature > 23 -> TemperatureDescriptions.temperatureDescriptions["hot"] ?: "Hot"
            else -> ""
        }

        // Snow conditions check
        if (averageTemperature < 0 && hasRain) {
            WeatherDescriptions.weatherDescriptions["snow"] ?: "Snowy"
        }

        // Building the final description
        val descriptionBuilder = StringBuilder(weatherDescription + temperatureDescription)
        maxTemperature?.let { descriptionBuilder.append("\n\nHøyeste temperatur er $it°C. ") }
        minTemperature?.let { descriptionBuilder.append("\nLaveste temperatur er $it°C. ") }

        return descriptionBuilder.toString()
    }

}



