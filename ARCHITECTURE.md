# ARCHITECTURE

## MVVM & UDF 🌊
The architecture follows the MVVM and UDF design patterns with a two-layered architecture consisting of a UI-layer and a data layer. A domain layer has been omitted due to a lack of need to substantially change the data given by the used API's. 

The following example shows how we manage and display the current chance of precipitation. 

[`Click here`](#tag) to scroll down to a diagram of the architecture. 

### Model

The `ForecastWorker` at `no/uio/ifi/in2000/weatheru/data/forecast/ForecastWorker.kt` is responsible for updating the database and instantiating the ForecastRepo. To make sure requests are sent for the correct location, the worker also checks the location of the device. 
    
```
class ForecastWorker(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        try {
            val forecastRepo = ForecastRepo()
            val databaseInstance = ApiDatabase.getDatabase(applicationContext).forecastStateDao()
            val id = inputData.getInt("id", 3)
            if (id == 3) { // No id retrieved
                return Result.failure()
            }
            val valueOfPreviousLastChange: String = databaseInstance.getLastChangeInfo(id)

            return if (isNetworkAvailable(applicationContext)) {
                val coordinates: Location = getCoordinates() ?: return Result.failure()
                handleInternetAvailable(forecastRepo, coordinates, valueOfPreviousLastChange, databaseInstance, id)
            } else {
                val coordinates: Location = databaseInstance.getLocation(id) ?: return Result.retry() 
                handleNoInternet(forecastRepo, databaseInstance, valueOfPreviousLastChange, coordinates, id)
            }
        }
    }

    private suspend fun handleInternetAvailable(forecastRepo: ForecastRepo, coordinates: Location?, valueOfPreviousApiUpdate: String, databaseInstance: ForecastStateDao, id: Int): Result {
        val (latitude, longitude) = coordinates
        val dataDeserialized = forecastRepo.getForecastDeserialized(latitude, longitude)
        val lastApiUpdate = forecastRepo.getLastChange(dataDeserialized)
        val lastLocation: Location? = databaseInstance.getLocation(id)

        return if (lastApiUpdate != valueOfPreviousApiUpdate || lastLocation != coordinates) {
            if (lastLocation != coordinates) {
                // Either location or API has been updated
                insertNewForecastValuesIntoDatabase(forecastRepo, dataDeserialized, databaseInstance, coordinates, id)
                Result.success()
            } else {
                // Nothing new should be inserted
                Result.success()
            }
        }
    }

    private suspend fun insertNewForecastValuesIntoDatabase(forecastRepo: ForecastRepo, dataDeserialized: LocationForecast, databaseInstance: ForecastStateDao, coordinates: Location, id: Int) {
        val currentChanceOfRain = forecastRepo.getCurrentPrecipitation(dataDeserialized)
        val forecastState = ForecastDataState(
            currentChanceOfRain = currentChanceOfRain
        )
    }
}
```

The data is updated through immutable data classes, such as `ForecastDataState` found at `no/uio/ifi/in2000/weatheru/model/forecast/ForecastDataState.kt`. It contains many more properties, but is shortened here for an easier read in this example.
```
data class ForecastDataState(
    val currentChanceOfRain: Double? = null
)
```

`ForecastDatasource` retrieves data from the API and transforms the data for our data class `LocationForecast` at `no/uio/ifi/in2000/weatheru/model/forecast/LocationForecast.kt`. The `LocationForecast`is split into multiple data classes for easier retrieval. We have shortened the path in this example, but to summarize, we extract an instant Timeseries object, which will give us the current chance of precipitation. You can check the getter function further down to see the full path. Notice that we use our own `NetworkClient` to send our API calls through the IFI-proxy server. 
```
return try {
    if (!areValidCoordinates(latitude, longitude)) return null
    val response = NetworkClient.metClient.get("weatherapi/locationforecast/2.0/compact?lat=$latitude&lon=$longitude")
    if (response.status.isSuccess()) {
        val result = response.body<LocationForecast>()
        result.copy(timeZoneData = timeZoneData ?: TimeZoneData()) 
    } else {
        Log.e("ForecastDatasource", "Failed to fetch forecast: HTTP ${response.status.value}")
        null
    }
} catch (e: Exception) {
    Log.e("ForecastDatasource", "Error deserializing forecast: ${e.message}")
    null
}
```

```
data class Details(
    val precipitation_amount: Double
)
```
  
 The `ForecastRepo` found at `no/uio/ifi/in2000/weatheru/data/forecast/ForecastRepo.kt` is initialized by the worker to initiate the API-calls through the `ForecastDatasource` mentioned above. It then uses the deserialized forecast data to offer getters for the various weather data.


  ```
suspend fun getForecastDeserialized(latitude: Double, longitude: Double): LocationForecast? {
    val timeZoneData = fetchTimeZone(APIKEY, latitude, longitude)
    return deserializeForecast(latitude, longitude, timeZoneData)
}
```
```
fun getCurrentPrecipitation(deserializedData: LocationForecast): Double {
    val currentData = deserializedData.properties.timeseries.firstOrNull()?.data?.next_1_hours?.details
    return currentData?.precipitation_amount ?: 0.0
}
``` 
The `ForecastWorker` then uses the functions offered by the repository to asynchronously update and cache data to a room database through the immutable data classes.
  
```
suspend fun insertNewForecastValuesIntoDatabase(forecastRepo: ForecastRepo, dataDeserialized: LocationForecast, databaseInstance: ForecastStateDao, coordinates: Location) {
    val currentChanceOfRain = forecastRepo.getCurrentPrecipitation(dataDeserialized)
}
```

### ViewModel 
The `HomeScreenViewModel` observes the Room database, and updates its own private UI state to the newest version of that database, which then causes the public version of that UI state to likewise update to the same values. In the `updateForecastUIState(id: Int), the int parameter represents what location ID to display on the HomeScreen. 

```    
private val _forecastUIState = MutableStateFlow(
    ForecastUIState(
        currentChanceOfRain = null
    )
)

val currentChanceOfRain = forecastRepo.getCurrentPrecipitation(dataDeserialized)
val forecastUIState: StateFlow<ForecastUIState> = _forecastUIState.asStateFlow()

private suspend fun updateForecastUIState(id: Int) {
try {
    forecastStateDao.getLatestForecastState(id)
        .collectLatest { newForecastState: ForecastStateView? ->
            if (newForecastState != null) { 
                withContext(Dispatchers.Main) {
                    _forecastUIState.update { currentUIState ->
                        currentUIState.copy(
                            currentChanceOfRain = newForecastState.currentChanceOfRain
                        )
                    }
                }
            }
        }
    }
}
```

### View
The Cards found in `no/uio/ifi/in2000/weatheru/ui/home/cards` observes the UI state from the viewModel.
```
fun RainCard(viewModel: HomeScreenViewModel) {
    val forecastUIState by viewModel.forecastUIState.collectAsState()
    val currentChanceOfRain = forecastUIState.currentChanceOfRain
}
```

The UI itself cannot make any changes to the values in that is given to it from the viewModel. This is done to uphold the principle of a "single source of truth" for its information.

##  <a name="tag"></a>A Unidirectional data flow 

[![](https://mermaid.ink/img/pako:eNp9k91ugkAQhV-FTG_RgMjfNmnS1DT1wqQptU0ablYYKnFhybJEqfruBVGzRpSr4cx3Ziczu1uIeIxAIGF8HS2pkNrnJMy15nsXfFNrg8HTC0sxl53Yxa36ygVGtJQTKmnJKxFhR1zrKv2BBb_kWqUldqoxkFTi7sx8c7FCcenrtBvO5yJtfxa0xFuu644Ukwqoh7_xDINIIOZfKa5nzehYi14dd4M7dzqfdn3Opx0fMVqWE0y0Bd8EsmaoJSlj5CHxE72Ugq-QPFiWdYwH6zSWSzIqNo9hrhQ4ruxUQ80c99aX6lnYPeywr3vAccR9iDrhvnzf2Pq4-VSRQYcMRUbTuLnG2xYLQS4xwxBIE8aY0IrJEMJ836C0kjyo8wiIFBXqUBVxs4hJSn8FzYAklJWNWtD8h_PsBGGcSi5m3Us5PJgDAmQLGyCmZwzHY29k2r7jWr7t6lADsY2hbZqe65uWNxo7pmPvdfg7FDWGnmXarm8bjuW7o7Fr7f8Bfrwx3g?type=png)](https://mermaid.live/edit#pako:eNp9k91ugkAQhV-FTG_RgMjfNmnS1DT1wqQptU0ablYYKnFhybJEqfruBVGzRpSr4cx3Ziczu1uIeIxAIGF8HS2pkNrnJMy15nsXfFNrg8HTC0sxl53Yxa36ygVGtJQTKmnJKxFhR1zrKv2BBb_kWqUldqoxkFTi7sx8c7FCcenrtBvO5yJtfxa0xFuu644Ukwqoh7_xDINIIOZfKa5nzehYi14dd4M7dzqfdn3Opx0fMVqWE0y0Bd8EsmaoJSlj5CHxE72Ugq-QPFiWdYwH6zSWSzIqNo9hrhQ4ruxUQ80c99aX6lnYPeywr3vAccR9iDrhvnzf2Pq4-VSRQYcMRUbTuLnG2xYLQS4xwxBIE8aY0IrJEMJ836C0kjyo8wiIFBXqUBVxs4hJSn8FzYAklJWNWtD8h_PsBGGcSi5m3Us5PJgDAmQLGyCmZwzHY29k2r7jWr7t6lADsY2hbZqe65uWNxo7pmPvdfg7FDWGnmXarm8bjuW7o7Fr7f8Bfrwx3g)

## Object Oriented Code Principles 🖇️
The principle of low coupling is also followed by minimizing the number of dependencies per component. For example are all API calls sent from our worker functions, which update the database tables independently of the ViewModel. This is with the exception of geocoding, which is handled straight from our viewModel to our nominatim repository due to how its functionality is directly connected to location searching by the user. It does, therefore, not give rise to any caching necessities or similar, which makes sending data through the ApiManager unnecessary.

The principle of high cohesion is followed by limiting the responsibility of each object. For example is the class `PermissionHandler` responsible for handling (location) permissions, whilst class `LocationMonitor` manages continual location checks that update the "_location" UI state in our viewModel based on location changes from the used device.

## File Structure 🗂️

The app files are divided into the main folders `data`, `model`, `network`, `ui`, and `util`.
* `model` contains DAOs and data classes belong to alerts, forecast and sunrise. You'll also find the descriptions of weather and temperature here.
* `data` contains repositories and the classes that perform the API calls; the -`Datasource` classes contain the HTTP-clients, and the -`Worker`functions manage caching and requesting new fetches.
* `ui` contains everything GUI-related, mainly `Composable` functions and data classes for managing state, plus our custom Material Design theming.
* `utils` contains various utility tools, such as enum class `InternetStatus`, converters for types and time formats, delay-functions and lifecycle observers.
```
├── MainActivity.kt
├── data
│   ├── alerts
│   ├── forecast
│   ├── nominatim
│   ├── sunrise
│   └── timezone
├── model
│   ├── alerts
│   ├── descriptions
│   ├── forecast
│   ├── nominatim
│   ├── sunrise
│   └── timezone
├── ui
│   ├── home
│   │   ├── UIState
│   │   ├── cards
│   ├── location
│   │   └── locationCards
│   ├── navigation
│   ├── notifications
│   ├── settings
│   └── theme
│       └── material_theming
└── util
```

## API Level 🛠️
The app's minimum Android API level requirement is level 26, which corresponds to Android version 8 `Oreo`. Given that the number of devices that use Android versions older than 7 represents less than 10% of the market, we have prioritized developing our app for more efficient and secure proof versions.  
