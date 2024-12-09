package no.uio.ifi.in2000.weatheru.ui.location

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel

class LocationPermissionHandler {
    private val locationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
    private val locationPermissionRequestCode = 1

    private lateinit var viewModel: HomeScreenViewModel
    private lateinit var activity: Activity

    fun setActivity(activity: Activity) {
        this.activity = activity
        Log.e("setActivity", "activity value: $this")
    }

    fun setViewModel(viewModel: HomeScreenViewModel) {
        this.viewModel = viewModel
    }




    fun checkLocationPermission() {
        Log.e("checkLocationPermission", "activity value: $this")
        if (ActivityCompat.checkSelfPermission(
                activity,
                locationPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //Log.e("checkLocationPermission", "activity value: $activity")
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(locationPermission),
                locationPermissionRequestCode
            )
        } else {
            viewModel.updateLocationPermissionStatus(true)
        }
    }
}