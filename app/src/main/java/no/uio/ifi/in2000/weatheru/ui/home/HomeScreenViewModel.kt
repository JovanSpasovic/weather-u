package no.uio.ifi.in2000.weatheru.ui.home


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.weatheru.data.ApiManager
import no.uio.ifi.in2000.weatheru.data.nominatim.NominatimRepo
import no.uio.ifi.in2000.weatheru.model.ApiDatabase
import no.uio.ifi.in2000.weatheru.model.alerts.AlertsDataState
import no.uio.ifi.in2000.weatheru.model.forecast.ForecastStateView
import no.uio.ifi.in2000.weatheru.ui.UIState.AlertsUIState
import no.uio.ifi.in2000.weatheru.ui.UIState.ForecastUIState
import no.uio.ifi.in2000.weatheru.ui.location.Location
import no.uio.ifi.in2000.weatheru.ui.location.LocationMonitor
import no.uio.ifi.in2000.weatheru.ui.location.LocationWithName
import no.uio.ifi.in2000.weatheru.util.LocationPermissionStatus
import no.uio.ifi.in2000.weatheru.util.NetworkMonitor
import no.uio.ifi.in2000.weatheru.util.SharedPreferencesManager
import kotlin.coroutines.cancellation.CancellationException

/**
 * HomeScreenViewModel is a ViewModel class that handles the business logic for the Home Screen.
 * It interacts with the API Manager, Network Monitor, Location Monitor, and Shared Preferences Manager.
 * It also manages the UI states for the Forecast and Alerts.
 *
 * @property apiManager An instance of ApiManager to handle API calls.
 */
class HomeScreenViewModel(private val apiManager: ApiManager): ViewModel() {

    private val appContext = apiManager.getApplicationContext()
    private val networkMonitor = NetworkMonitor(appContext)
    private val locationMonitor = LocationMonitor(appContext)
    private val sharedPreferencesManager = SharedPreferencesManager(appContext)

    private val db = ApiDatabase.getDatabase(appContext)
    private val alertStateDao = db.alertStateDao()
    private val forecastStateDao = db.forecastStateDao()

    private val nominatimRepo = NominatimRepo()

    private val currentLocation: StateFlow<Location?> get() = locationMonitor.location
    val networkStatus: StateFlow<Boolean> get() = networkMonitor.networkStatus

    private val _currentLocationWithName = MutableStateFlow(loadLocationWithName())
    val currentLocationWithName = _currentLocationWithName.asStateFlow()

    private val _locationPermissionStatus = MutableStateFlow(LocationPermissionStatus.UNDETERMINED)
    val locationPermissionStatus get() = _locationPermissionStatus.asStateFlow()

    private val _showLoadingAnimation = MutableStateFlow(false)
    val showLoadingAnimation get() = _showLoadingAnimation.asStateFlow()

    private val _locationList = MutableStateFlow(loadLocationList())
    val locationList get() = _locationList.asStateFlow()

    private val _searchResults = MutableStateFlow(emptyList<LocationWithName>())
    val searchResults get() = _searchResults.asStateFlow()

    //  denotes whether the user is looking at their current location (id 0) or another
    //  location(id 1) in their homeScreen.
    private val _currentlyUsedLocationId = MutableStateFlow(loadCurrentlyUsedLocationId())

    private val _currentTheme = MutableStateFlow(loadSavedTheme())
    val currentTheme get() = _currentTheme.asStateFlow()

    private val _notificationToggleState = MutableStateFlow(loadSavedNotificationToggleState())
    val notificationToggleState get() = _notificationToggleState.asStateFlow()

    //  Separate UI states for different repos.
    private val _forecastUIState = MutableStateFlow(ForecastUIState())
    val forecastUIState: StateFlow<ForecastUIState> = _forecastUIState.asStateFlow()

    private val _alertsUIState = MutableStateFlow (AlertsUIState())
    val alertsUIState: StateFlow<AlertsUIState> = _alertsUIState.asStateFlow()


    /**
     * This function is called when the ViewModel is initialized.
     * It starts the Network Monitor, updates the API data from the current location,
     * and fetches the latest Forecast and Alert states.
     */
    init {
        startNetworkMonitor()
        updateApiDataFromCurrentLocation()
        fetchLatestForecastState()
        fetchLatestAlertState()
    }

    /**
     * This function starts the Location Monitor by registering location updates.
     * It is only started if permissions are given.
     */
    private fun startNetworkMonitor() {
        viewModelScope.launch {
            networkMonitor.registerNetworkCallback()
        }
    }
    /**
     * This is started only if permissions are give
     */
    fun startLocationMonitor() {
        viewModelScope.launch {
            locationMonitor.registerLocationUpdates()
        }
    }



    //  End viewModel
    override fun onCleared() {
        super.onCleared()
        locationMonitor.unregisterLocationUpdates()
        networkMonitor.unregisterNetworkCallback()
    }

    /**
     * This is a public function that handles a change in permissions from GRANTED to DENIED.
     * It causes the location monitor to unregister.
     * This function handles cases where location has not yet been registered.
     */
    fun stopLocationMonitor() {
        viewModelScope.launch {
            locationMonitor.unregisterLocationUpdates()
        }
    }

    //  Get the current location permission status the user has given the app.
    fun getCurrentLocationPermissionStatus(): LocationPermissionStatus {
        return locationPermissionStatus.value
    }

    //  Update the location permission status if user changed their permissions.
    fun updateLocationPermissionStatus(isGranted: Boolean) {
        _locationPermissionStatus.value =
            if (isGranted) LocationPermissionStatus.GRANTED else LocationPermissionStatus.DENIED
    }


    /**
     * Observes the user's current location from the values given by LocationMonitor. If the location changes,
     * the new location gets updated into the database. The user will only see this change if they view their
     * current location in the homeScreen, to avoid interfering with user experience.
     */
    fun updateApiDataFromCurrentLocation() {
        viewModelScope.launch {
            try {
                currentLocation
                    .collectLatest {
                        it?.let {
                            Log.e("viewModel", "updating location from location callback")

                            updateCurrentLocationWithName(it.latitude, it.longitude)
                            apiManager.updateCurrentForecastOnce(
                                it.latitude,
                                it.longitude,
                                CURRENT_LOCATION_DATABASE
                            )
                            apiManager.updateCurrentAlertsContinually(
                                it.latitude,
                                it.longitude,
                                CURRENT_LOCATION_DATABASE
                            )

                        }
                    }
            } catch (e: CancellationException) {
                Log.e("updateForecastData", "Coroutine was cancelled", e)
            } catch (e: Exception) {
                Log.e("updateForecastData", "Exception... stacktrace:")
                e.printStackTrace()
            }
        }
    }

    /**
     * This function is called from "updateApiDataFromCurrentLocation()". It uses reverse geocoding to update the name
     * and values of the "current location" card within the location screen. This ensures that the current location card
     * reflects the user's current location and is not outdated.
     */
    private fun updateCurrentLocationWithName(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val deserializedGeocoding = nominatimRepo.fetchReverseGeocoding(latitude, longitude)
                val addressAndCountry = nominatimRepo.getAddressAndCountry(deserializedGeocoding)
                val locationName = "${addressAndCountry.first?.name ?: ""}, ${addressAndCountry.second}"
                val locationWithName = LocationWithName(locationName, Location(latitude, longitude))

                withContext(Dispatchers.Main) {
                    _currentLocationWithName.value = locationWithName
                }

                sharedPreferencesManager.saveLocationWithName(locationWithName)
            } catch (e: Exception) {
                Log.e(
                    "HomeScreenViewModel",
                    "Unexpected error in updateCurrentLocationWithName, likely non-flow related."
                )
                e.printStackTrace()
            }
        }
    }

    /**
     * Updates the location id to 0, which means that weather data from the user's
     * current location will be shown on the main screen.
     */
    fun showCurrentLocationOnHomeScreen() {
        viewModelScope.launch {
            updateCurrentlyUsedLocationId(CURRENT_LOCATION_DATABASE)
        }
    }

    /**
     * Updates the home screen to show a location that is not the user's own current location.
     * Because other locations are not automatically updated, an API-call has to be started
     * based on the coordinates of that new location.
     */
    fun updateOtherLocationFromLocationScreen(location: Location) {
        viewModelScope.launch {
            updateCurrentlyUsedLocationId(OTHER_LOCATION_DATABASE)

            apiManager.updateForecastOnce(
                location.latitude,
                location.longitude,
                OTHER_LOCATION_DATABASE
            )
            apiManager.updateAlertsOnce(
                location.latitude,
                location.longitude,
                OTHER_LOCATION_DATABASE
            )
        }
    }





    private var updateForecastJob: Job? = null

    /**
     * Starts observing new forecastStates from the database. This function specifically observes the
     * location id state, which indicates whether to observe the user's current location (id 0) or
     * other locations (id 1), and calls "updateForecastUIState()" based on that id.
     */
    private fun fetchLatestForecastState() {
        viewModelScope.launch {
            _currentlyUsedLocationId.collect { id ->
                updateForecastJob?.cancel()
                delay(100)
                Log.e("viewModel","current database id in Forecast: ${_currentlyUsedLocationId.value}")
                updateForecastJob = launch(Dispatchers.IO) {//  Start a new IO coroutine without blocking the location id collection
                    updateForecastUIState(id)
                }
            }
        }
    }

    /**
     * Observes the database based on location id, and inserts new values into _forecastUIState.
     * @param id The location id which indicates whether to observe the user's current location (id 0) or
     * other locations (id 1).
     */
    private suspend fun updateForecastUIState(id: Int) {
        try {
            forecastStateDao.getLatestForecastState(id)
                .collectLatest { newForecastState: ForecastStateView? ->
                    if (newForecastState != null) { // For some reason this makes everything work
                        withContext(Dispatchers.Main) {
                            _forecastUIState.update { currentUIState ->
                                //Log.e("ViewModel", "newForecastState value: $newForecastState")
                                currentUIState.copy(
                                    currentWindSpeed = newForecastState.currentWindSpeed,
                                    currentWindFromDirection = newForecastState.currentWindFromDirection,
                                    hourlyWindSpeedAndDirectionForNext24Hours = newForecastState.hourlyWindSpeedAndDirectionForNext24Hours,

                                    currentChanceOfRain = newForecastState.currentChanceOfRain,
                                    hourlyRainForNext10Hours = newForecastState.hourlyRainForNext10Hours,
                                    hourlyRainForNext24Hours = newForecastState.hourlyRainForNext24Hours,
                                    rainForNext7Days = newForecastState.rainForNext7Days,
                                    currentSymbolCode = newForecastState.currentSymbolCode,
                                    temp10Hours = newForecastState.temp10Hours,
                                    tempTomorrow24h = newForecastState.tempTomorrow24h,

                                    //Main-Card
                                    currentTemperature = newForecastState.currentTemperature,
                                    currentDate = newForecastState.currentDate,
                                    currentTemperatureCode = newForecastState.currentTemperatureCode,
                                    temp7Hours = newForecastState.temp7Hours,

                                    //Max and min temperatures
                                    mintemperatures7xdays = newForecastState.minTemperatures5Days,
                                    maxtemperature7xdays = newForecastState.maxTemperature5Days,
                                    getDailyDateAndCloudInfo = newForecastState.dailyDateAndCloudInfo,
                                    isThereAnyRainToday = newForecastState.isThereAnyRainToday,

                                    displayName = newForecastState.displayName,
                                    address = newForecastState.address,
                                    country = newForecastState.country,
                                    sunrise = newForecastState.sunrise,
                                    sunset = newForecastState.sunset,
                                    timeZone = newForecastState.timeZone,
                                    description = newForecastState.description,
                                )
                            }
                            //Log.e("viewModel","forecast ui state has been updated: $newForecastState")
                            _showLoadingAnimation.value = false
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(
                "HomeScreenViewModel",
                "Unexpected error in updateForecastUIState, likely non-flow related."
            )
            e.printStackTrace()
        }
    }


    private var updateAlertsJob: Job? = null
    /**
     * Starts observing new alertStates from the database. This function specifically observes the
     * location id state, which indicates whether to observe the user's current location or
     * other locations, and calls "updateAlertsUIState()" based on that id.
     */
    private fun fetchLatestAlertState() {
        viewModelScope.launch {
            _currentlyUsedLocationId.collect { id ->
                updateAlertsJob?.cancel()
                delay(300)  // Don't know why, but this fixed the issue
                Log.e("viewModel","current database id in Alerts: ${_currentlyUsedLocationId.value}")
                updateAlertsJob = launch(Dispatchers.IO) {
                    updateAlertsUIState(id)
                }
            }
        }
    }

    /**
     * Observes the database based on location id, and inserts new values into _alertsUIState.
     * @param id The location id which indicates whether to observe the user's current location (id 0) or
     * other locations (id 1).
     */    private suspend fun updateAlertsUIState(id: Int) {
        try {
            alertStateDao.getLatestAlertState(id)
                .collectLatest { newAlertsState: AlertsDataState? ->
                    //Log.e("viewModel","newAlertsState value: $newAlertsState")

                    if (newAlertsState != null) {
                        withContext(Dispatchers.Main) {
                            //  Notification will still be given, however.
                            _alertsUIState.update { currentUIState ->
                                currentUIState.copy (
                                    eventAwarenessName = newAlertsState.eventAwarenessName,
                                    description = newAlertsState.description,
                                    consequences = newAlertsState.consequences,
                                    instruction = newAlertsState.instruction,
                                    eventEndingTime = newAlertsState.eventEndingTime,
                                    event = newAlertsState.event,
                                    matrixColor = newAlertsState.matrixColor
                                )
                            }
                            //Log.e("viewModel","alerts ui state has been updated: $newAlertsState")
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(
                "HomeScreenViewModel",
                "Unexpected error in updateAlertsUIState, likely non-flow related."
            )
            e.printStackTrace()
        }
    }



    //  Must give location permission for use of rain notification.
    //  Only makes a rain notification for your current location.
    private var rainNotificationJob: Job? = null

    /**
     * Handles rain notifications for the user's current location.
     * The user must grant location permission for this feature to work.
     */
    fun startRainNotifications() {
        rainNotificationJob?.cancel()   //  Cancel Job in case something went wrong with shared preferences and toggle state was wrong
        rainNotificationJob = viewModelScope.launch {
            currentLocation.collectLatest { newLocation ->
                    if (newLocation != null) {
                        withContext(Dispatchers.IO) {
                            forecastStateDao.getTimeZoneFlow(CURRENT_LOCATION_DATABASE).collect {
                                apiManager.startRainNotifications(
                                    newLocation.latitude,
                                    newLocation.longitude,
                                    forecastUIState.value.timeZone ?: ""
                                )
                            }
                        }
                    }
                }
        }
    }


    fun stopRainNotifications() {
        viewModelScope.launch {
            apiManager.cancelRainNotifications()
            rainNotificationJob?.cancel()
            rainNotificationJob = null
        }
    }






    /**
     * Sets the location to Oslo if the user denies location permission.
     * The user can choose another location to view or grant permissions at any time.
     */
    fun setLocationToOslo() {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val latitude = 59.9139
                val longitude = 10.7522

                apiManager.updateForecastOnce(
                    latitude,
                    longitude,
                    CURRENT_LOCATION_DATABASE
                )
                apiManager.updateAlertsOnce(
                    latitude,
                    longitude,
                    CURRENT_LOCATION_DATABASE
                )
            } catch (e: CancellationException) {
                // Handle cancellation specifically, if needed
                Log.e("setLocationToOslo", "Coroutine was cancelled", e)
            } catch (e: Exception) {
                Log.e("setLocationToOslo", "Exception... stacktrace:")
                e.printStackTrace()
            }
        }
    }


    /**
     * Uses geocoding for the user-search, and updates a list of locations
     * that might match the query.
     * @param query The search query entered by the user.
     */
    fun updateSearchResults(query: String) {
        viewModelScope.launch {
            if (query == "") {
                _searchResults.value = emptyList()
            } else {
                val deserializedGeocoding = nominatimRepo.fetchGeocoding(query)
                val newSearchResults = nominatimRepo.getLocationWithNameList(deserializedGeocoding)

                if (newSearchResults != null) {
                    _searchResults.value = newSearchResults
                } else {
                    _searchResults.value = listOf(LocationWithName("Location not found", Location()))
                }
            }
        }
    }

    //  Empty the search result list.
    fun eraseSearchResult() {
        _searchResults.value = emptyList()
    }



    //  -- Loading from and saving into Shared preferences --


    //  Get list of favorite locations in the locationScreen
    private fun loadLocationList() : List<LocationWithName> {
        return sharedPreferencesManager.loadLocationList()
    }

    /**
     * Adds a new location to the list of favorite locations in the location screen.
     * @param location The location to be added to the list of favorite locations.
     */
    fun addLocationToLocationList(location: LocationWithName) {
        val updatedList = locationList.value.toMutableList()


        //  Only add location to location list if it is not already there.
        //  Is handled in LocationScreen, but is added here for safety
        if (!updatedList.contains(location)) {
            updatedList.add(location)
            _locationList.value = updatedList

            // Save location list to shared preferences
            sharedPreferencesManager.saveLocationList(updatedList)
        }
    }

    /**
     * Removes a location from the list of favorite locations in the location screen if it is found.
     * @param location The location to be removed from the list of favorite locations.
     */
    fun removeLocationFromLocationList(location: LocationWithName) {
        val updatedList = locationList.value.toMutableList().apply {
            remove(location)
        }
        _locationList.value = updatedList

        //  save location list to shared preferences
        sharedPreferencesManager.saveLocationList(updatedList)
    }


    private fun loadLocationWithName(): LocationWithName {
        return sharedPreferencesManager.loadLocationWithName()
    }

    /**
     * Loads the previous toggle state for rain notifications from shared preferences.
     * @return Boolean value indicating whether the user wanted to get rain notifications.
     */
    private fun loadSavedNotificationToggleState(): Boolean {
        return sharedPreferencesManager.loadNotificationToggle()
    }

    /**
     * Loads the previous toggle state for rain notifications from shared preferences.
     * @return Boolean value indicating whether the user wanted to get rain notifications.
     */
    fun updateNotificationToggleState(bool: Boolean) {
        viewModelScope.launch {
            if (_notificationToggleState.value != bool) {
                _notificationToggleState.value = bool
                sharedPreferencesManager.saveNotificationToggle(bool)
            }
        }
    }

    /**
     * Loads the currently used location id from shared preferences.
     * This id dictates whether the user currently wants to view their current position (id 0),
     * or if they want to view another place in the world (id 1).
     * @return Int value indicating the currently used location id.
     */
    private fun loadCurrentlyUsedLocationId(): Int {
        return sharedPreferencesManager.loadLocationId(CURRENT_LOCATION_DATABASE) // Fallback value as parameter
    }

    /**
     * Updates the currently used location id based on user input.
     * This id dictates whether the user currently wants to view their current position (id 0),
     * or if they want to view another place in the world (id 1).
     * @param id The new location id to be used.
     */
    private fun updateCurrentlyUsedLocationId(id: Int) {
        if (_currentlyUsedLocationId.value != id) {
            Log.e("viewModel", "Setting location ID to $id")
            _currentlyUsedLocationId.value = id


            //  save state to shared preferences
            sharedPreferencesManager.saveLocationId(id)
        }
    }



    /**
     * Loads the previously used theme from shared preferences.
     * @return String value indicating the code of the previously used theme.
     */
    private fun loadSavedTheme(): String {
        return sharedPreferencesManager.loadTheme("gradient")
    }

    /**
     * Updates the theme of the entire application based on user input.
     * @param themeCode The code of the new theme to be applied.
     */
    fun updateTheme(themeCode: String) {
        _currentTheme.value = themeCode

        //  Save theme with shared preferences
        sharedPreferencesManager.saveTheme(themeCode)
    }


    /**
     * Attempts to fetch a new location again after encountering internet problems.
     * This function is used to retry the operation of updating the API data from the current location.
     */
    fun retryForecastData() {
        updateApiDataFromCurrentLocation()
    }


    companion object {
        const val CURRENT_LOCATION_DATABASE = 0
        const val OTHER_LOCATION_DATABASE = 1
    }
}



