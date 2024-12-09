package no.uio.ifi.in2000.weatheru.data.forecast

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import no.uio.ifi.in2000.weatheru.data.nominatim.NominatimRepo
import no.uio.ifi.in2000.weatheru.data.sunrise.SunriseRepo
import no.uio.ifi.in2000.weatheru.model.ApiDatabase
import no.uio.ifi.in2000.weatheru.model.forecast.ForecastDataState
import no.uio.ifi.in2000.weatheru.model.forecast.ForecastStateDao
import no.uio.ifi.in2000.weatheru.model.forecast.LocationForecast
import no.uio.ifi.in2000.weatheru.model.forecast.Timeseries
import no.uio.ifi.in2000.weatheru.ui.location.Location
import no.uio.ifi.in2000.weatheru.util.isNetworkAvailable
import java.io.IOException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ForecastWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {
        try {
            val forecastRepo = ForecastRepo()
            val databaseInstance = ApiDatabase.getDatabase(applicationContext).forecastStateDao()
            val id = inputData.getInt("id", 3)
            if (id == 3) {
                Log.e("ForecastWorker", "Wrong ID from Input data")
                return Result.failure()
            }
            val valueOfPreviousLastChange: String = databaseInstance.getLastChangeInfo(id)

            return if (isNetworkAvailable(applicationContext)) {
                val coordinates: Location = getCoordinates() ?: return Result.failure()
                handleInternetAvailable(forecastRepo, coordinates, valueOfPreviousLastChange, databaseInstance, id)
            } else {
                val coordinates: Location = databaseInstance.getLocation(id) ?: return Result.retry() // Retry to check network again
                handleNoInternet(forecastRepo, databaseInstance, valueOfPreviousLastChange, coordinates, id)
            }
        } catch (e: IOException) {
            Log.e("ForecastWorker", "IOException, retrying... ", e)
            return Result.retry()
        } catch (e: Exception) {
            Log.e("ForecastWorker", "An unknown error occurred: ", e)
            return Result.failure()
        }
    }



    private suspend fun handleInternetAvailable(forecastRepo: ForecastRepo, coordinates: Location?, valueOfPreviousApiUpdate: String, databaseInstance: ForecastStateDao, id: Int): Result {
        Log.d("ForecastWorker", "Internet access available. Checks if database has been updated:")

        if (coordinates == null) {
            Log.e("ForecastWorker", "Database could not access the given coordinates")
            return Result.failure()
        }
        val (latitude, longitude) = coordinates

        val dataDeserialized = forecastRepo.getForecastDeserialized(latitude, longitude)
        if (dataDeserialized == null) {
            Log.e("ForecastWorker", "Failed to deserialize forecast API")
            return Result.failure()
        }

        val lastApiUpdate = forecastRepo.getLastChange(dataDeserialized)
        val lastLocation: Location? = databaseInstance.getLocation(id)
        //Log.e("ForecastWorker", "lastLocation: $lastLocation\n" +
        //        "coordinates: $coordinates")

        //  Checks if either the API time or location has changed since last access
        return if (lastApiUpdate != valueOfPreviousApiUpdate || lastLocation != coordinates) {
            if (lastLocation != coordinates) {
                Log.d("ForecastWorker", "New location found. Tries to insert into database:")
               //Log.e("ForecastWorker", "Coordinate values: $coordinates")
            } else {
                Log.d("ForecastWorker", "API has been updated. Tries to insert new:")
            }
            insertNewForecastValuesIntoDatabase(forecastRepo, dataDeserialized, databaseInstance, coordinates, id)
            Result.success()
        } else {
            Log.d("ForecastWorker","API is not new. No insertion will be done:")
            //Log.d("ForecastWorker","database value: ${databaseInstance.getLatestForecastState().first()}")

            Result.success()
        }
    }



    private suspend fun handleNoInternet(forecastRepo: ForecastRepo, databaseInstance: ForecastStateDao, valueOfPreviousLastChange: String, coordinates: Location?, id: Int): Result {
        Log.d("ForecastWorker", "No internet access. Checks if current time matches time of database:")
        if (valueOfPreviousLastChange.isEmpty()) {    // somehow the value can be null
            Log.d("ForecastWorker", "Database has not been instantiated")
            return Result.success()
        }


        val timeZone = databaseInstance.getTimeZone(id)

        val currentHourlyTimeInUTC = getCurrentHourlyTimeInUTC(timeZone) ?: return Result.failure()


        //  Checks if API time has changed since last access
        return if (currentHourlyTimeInUTC != valueOfPreviousLastChange.substringBefore(":")) {
            Log.d("ForecastWorker", "Current time is different from database time. Tries to insert new values based on cache:")

            val locationForecast: LocationForecast? = databaseInstance.getLocationForecast(id)
            if (locationForecast == null) {
                Log.d("ForecastWorker", "Database has not been instantiated")
                return Result.success()
            }
            val timeseriesList: List<Timeseries> = locationForecast.properties.timeseries

            val updatedTimeseries = timeseriesList.dropWhile {
                it.time.substringBefore(":") != currentHourlyTimeInUTC
            }
            if (updatedTimeseries.isEmpty() || updatedTimeseries.size < 24) {
                Log.d("ForecastWorker", "Current time exceeds the limit of the cached data time interval. Returning:")
                Log.d("ForecastWorker", "Current time on device: $currentHourlyTimeInUTC " +
                        "\nCurrent time in database: ${valueOfPreviousLastChange.substringBefore(":")}")
                databaseInstance.insert(getEmptyForecastDataState())
                return Result.success()
            }

            //  Updates the database with cached values for the current time
            val cachedProperties = locationForecast.properties.copy(timeseries = updatedTimeseries)
            val cachedDataDeserialized = locationForecast.copy(properties = cachedProperties)

            insertNewForecastValuesIntoDatabase(forecastRepo, cachedDataDeserialized, databaseInstance, coordinates!!, id)

            return Result.success()
        } else {
            Log.d("ForecastWorker","Current time is equal to database time. No changes will be made.")
            Result.success()
        }
    }




    private fun getCurrentHourlyTimeInUTC(timeZone: String?): String? {

        if (timeZone == null) return null

        val zonedTime = ZonedDateTime.now(ZoneId.of(timeZone))

        //  Does not take into account timezones offset by 30 minutes
        val zoneOffsetInHours = zonedTime.offset.totalSeconds / 3600

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val timeStringUTC = zonedTime.minusHours(zoneOffsetInHours.toLong())

        val timeString = timeStringUTC.format(formatter)
        return timeString?.substringBefore(":")
    }



    private suspend fun insertNewForecastValuesIntoDatabase(forecastRepo: ForecastRepo, dataDeserialized: LocationForecast,
                                                            databaseInstance: ForecastStateDao, coordinates: Location, id: Int) {

        Log.d("ForecastWorker","Inserting new values into database:")
        val reverseGeocodingRepo = NominatimRepo()
        val sunriseRepo = SunriseRepo()

        //  Deserialize sunriseRepo only if internet is available
        val (sunrise, sunset) = if (isNetworkAvailable(applicationContext)) {
            val deserializedSunsetAPI = sunriseRepo.deserializeSunriseApi(coordinates.latitude, coordinates.longitude)
            val sunrise = sunriseRepo.getSunrise(deserializedSunsetAPI)
            val sunset = sunriseRepo.getSunset(deserializedSunsetAPI)
            sunrise to sunset
        } else {
            null to null //databaseInstance.getSunriseAndSunset(id) - will not give fully accurate results but should be close enough
        }

        //  Deserialize reverseGeoCoding only if internet is available
        val (address, country, displayName) = if (isNetworkAvailable(applicationContext)) {
            val deserializedReverseGeocoding = reverseGeocodingRepo.fetchReverseGeocoding(coordinates.latitude, coordinates.longitude)
            val addressAndCountry = reverseGeocodingRepo.getAddressAndCountry(deserializedReverseGeocoding)
            val address = addressAndCountry.first
            val country = addressAndCountry.second
            val displayName = reverseGeocodingRepo.getDisplayName(deserializedReverseGeocoding)
            Triple(address, country, displayName)
        } else {
            //  In case of no network connection, location will be same as before
            //  and can be found from database
            val address = databaseInstance.getAddress(id)
            val country = databaseInstance.getCountry(id)
            val name = databaseInstance.getDisplayName(id)
            Triple(address, country, name)
        }


        val temperatureList =
            forecastRepo.getHourlyTemperatureAndCloudsFor10Hours(dataDeserialized)
        val tempTomorrow24h = forecastRepo.getHourlyTemperatureAndCloudsForTomorrow24Hours(dataDeserialized)
        val currentChanceOfRain = forecastRepo.getCurrentPrecipitation(dataDeserialized)
        val hourlyRainForNext10Hours =
            forecastRepo.getHourlyRainForNext10Hours(dataDeserialized)
        val hourlyRainForNext24Hours =
            forecastRepo.getHourlyRainForNext24Hours(dataDeserialized)
        val rainForNext7Days = forecastRepo.getRainForNext7Days(dataDeserialized)
        val currentTemperature = forecastRepo.getCurrentTemperature(dataDeserialized)
        val currentDate = forecastRepo.getCurrentDate(dataDeserialized)
        val currentSymbolCode = forecastRepo.getCurrentSymbolCode(dataDeserialized)
        val currentTemperatureCode =
            forecastRepo.getCurrentTemperaturelCode(dataDeserialized)
        val temp7Hours = forecastRepo.getHourlyTemperatureFor7Hours(dataDeserialized)
        val maxTemperatures5Days =
            forecastRepo.getDailyMaxTemperaturesForNext7xDays(dataDeserialized)
        val minTemperatures5Days =
            forecastRepo.getDailyMinTemperaturesForNext7xDays(dataDeserialized)
        val getDailyDateAndCloudInfo =
            forecastRepo.getDailyDateAndCloudInfo(dataDeserialized)
        val currentWindSpeed = forecastRepo.getCurrentWindSpeed(dataDeserialized)
        val currentWindFromDirection =
            forecastRepo.getCurrentWindFromDirection(dataDeserialized)
        val hourlyWindSpeedAndDirectionForNext24Hours =
            forecastRepo.getHourlyWindSpeedAndDirectionForNext24Hours(dataDeserialized)
        val isThereAnyRainToday: Boolean = forecastRepo.isThereAnyRainToday(dataDeserialized)
        val lastChange = forecastRepo.getLastChange(dataDeserialized)
        val description = forecastRepo.getWeatherDescription(dataDeserialized)
        val timeZone = forecastRepo.getTimeZone(dataDeserialized)





        Log.d("ForecastWorker", "displayName for new Forecast location: $displayName")
        val forecastState = ForecastDataState(
            id = id,
            temp10Hours = temperatureList,
            tempTomorrow24h = tempTomorrow24h,
            currentChanceOfRain = currentChanceOfRain,
            hourlyRainForNext10Hours = hourlyRainForNext10Hours,
            hourlyRainForNext24Hours = hourlyRainForNext24Hours,
            rainForNext7Days = rainForNext7Days,
            currentWindSpeed = currentWindSpeed,
            currentWindFromDirection = currentWindFromDirection,
            hourlyWindSpeedAndDirectionForNext24Hours = hourlyWindSpeedAndDirectionForNext24Hours,
            currentSymbolCode = currentSymbolCode,
            currentTemperature = currentTemperature,
            currentDate = currentDate,
            currentTemperatureCode = currentTemperatureCode,
            temp7Hours = temp7Hours,
            maxTemperature5Days = maxTemperatures5Days,
            minTemperatures5Days = minTemperatures5Days,
            dailyDateAndCloudInfo = getDailyDateAndCloudInfo,
            isThereAnyRainToday = isThereAnyRainToday,
            lastChange = lastChange,
            locationForecast = dataDeserialized,
            locationPair = coordinates,
            address = address,
            country = country,
            displayName = displayName,
            sunrise = sunrise,
            sunset = sunset,
            timeZone = timeZone,
            description = description
        )


        Log.d("ForecastWorker","Inserting new values into database")
        //Log.d("ForecastWorker","new database value: $forecastState")
        //Log.e("ForecastWorker", "forecast in database: ${databaseInstance.getLatestForecastState().first()}")
        databaseInstance.insert(forecastState)
    }

    private fun getEmptyForecastDataState(): ForecastDataState {
        return ForecastDataState(
            id = 0,
            temp10Hours = null,
            tempTomorrow24h = null,
            currentChanceOfRain = null,
            hourlyRainForNext10Hours = null,
            hourlyRainForNext24Hours = null,
            rainForNext7Days = null,
            currentWindSpeed = null,
            currentWindFromDirection = null,
            hourlyWindSpeedAndDirectionForNext24Hours = null,
            currentSymbolCode = null,
            currentTemperature = null,
            currentDate = null,
            currentTemperatureCode = null,
            temp7Hours = null,
            maxTemperature5Days = null,
            minTemperatures5Days = null,
            dailyDateAndCloudInfo = null,
            isThereAnyRainToday = null,
            lastChange = "",
            locationForecast = null,
            locationPair = null,
            address = null,
            country = null,
            displayName = null,
            sunrise = null,
            sunset = null,
            description = null,
            timeZone = null
        )
    }

    private fun getCoordinates(): Location? {
        Log.d("ForecastWorker", "Tries to convert from Data to coordinates:")
        val latitude = inputData.getDouble("latitude", INVALID_DOUBLE)
        val longitude = inputData.getDouble("longitude", INVALID_DOUBLE)
        if (latitude == 0.0 || longitude == 0.0) {
            Log.e("ForecastWorker", "Something went wrong for coordinates. Got 0.0, so values were likely not found.")
        } else if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
            Log.e("ForecastWorker", "Coordinates out of range: Latitude: $latitude, Longitude: $longitude")
        }

        if (latitude == INVALID_DOUBLE || longitude == INVALID_DOUBLE) {
            Log.e("forecastWorked", "Exception: Database could not access given location coordinates")
            return null
        }

        return Location(
            latitude = latitude,
            longitude = longitude
        )
    }


    companion object {
        const val INVALID_DOUBLE: Double = 999.0
    }
}