package no.uio.ifi.in2000.weatheru.ui.location

import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationMonitor(context: Context) {


    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        interval = 30000
        fastestInterval = 10000
    }


    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> get() = _location.asStateFlow()


    fun registerLocationUpdates() {
        try {
            Log.d("startlocationUpdates", "Started continuous location checks:")
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            Log.e("startlocationUpdates", "Could not start continuous location checks:")
        } catch (e: Exception) {
            Log.e("startlocationUpdates", "Unknown exception in requestLocationUpdates:")

        }
    }

    fun unregisterLocationUpdates() {
        locationCallback.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                try {
                    val latestLocation = locationResult.lastLocation
                    if (latestLocation != null) {
                        _location.value = Location(latestLocation.latitude, latestLocation.longitude)
                    }
                } catch (e: Exception) {
                    Log.e("createLocationCallback", "Exception: $e")
                }
            }
            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                if (!locationAvailability.isLocationAvailable) {
                    Log.e("LocationUpdate", "Location data is not available")
                } else {
                    Log.e("LocationUpdate", "Location data is available!")

                }
            }
        }
    }

