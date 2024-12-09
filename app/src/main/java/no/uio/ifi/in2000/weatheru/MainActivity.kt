package no.uio.ifi.in2000.weatheru

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.weatheru.data.ApplicationClass
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModelFactory
import no.uio.ifi.in2000.weatheru.ui.location.LocationPermissionHandler
import no.uio.ifi.in2000.weatheru.ui.navigation.AppNavigation
import no.uio.ifi.in2000.weatheru.util.LocationPermissionStatus


class MainActivity : AppCompatActivity() {

    private val apiManager by lazy {
        (application as? ApplicationClass)?.apiManager ?: throw IllegalStateException("Application not initialized properly or apiManager not set")
    }

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            HomeScreenViewModelFactory(apiManager)
        )[HomeScreenViewModel::class.java]
    }

    private val locationPermissionHandler = LocationPermissionHandler()

    private lateinit var navController: NavHostController




    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermissionHandler.setActivity(this)
        locationPermissionHandler.setViewModel(viewModel)

        Log.e("location activity main", "activity value: $this")

        setContent {
            calculateWindowSizeClass(this)
            navController = rememberNavController()
            AppNavigation(viewModel, navController)

        }
        /**
         * On app start, and during app life cycle, constantly have a flag depicting the current
         * permission status from the user. This flag is updated whenever the permission status changes.
         * The flag is used to control the behavior of the app based on the user's location permission status.
         */
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.locationPermissionStatus.collect { status ->
                    when (status) {
                        LocationPermissionStatus.UNDETERMINED -> {
                            Log.d("enum class", "Permission UNDETERMINED. Requesting LocationChecks:")
                            locationPermissionHandler.checkLocationPermission()
                        }
                        LocationPermissionStatus.GRANTED -> {
                            Log.d("enum class", "Permission GRANTED. Calling viewModel's location update functions:")
                            viewModel.startLocationMonitor()
                        }
                        LocationPermissionStatus.DENIED -> {
                            Log.d("enum class", "Permission DENIED:")
                            viewModel.stopLocationMonitor()
                            viewModel.setLocationToOslo()
                    }
                }
            }
            }
        }
    }


    /**
     * Handles the result of the location permission request.
     * If the request code matches the location permission request code, it checks if the permission was granted.
     * If the permission was granted, it updates the location permission status in the ViewModel.
     *
     * @param requestCode The request code passed in requestPermissions().
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                val isGranted =
                    grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                viewModel.updateLocationPermissionStatus(isGranted)
            }
        }
    }


    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

}

