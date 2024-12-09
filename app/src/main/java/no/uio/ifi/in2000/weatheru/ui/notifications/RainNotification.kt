package no.uio.ifi.in2000.weatheru.ui.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import no.uio.ifi.in2000.weatheru.R
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel


@Composable
fun RainNotification(viewModel : HomeScreenViewModel) {

    val forecastUIState by viewModel.forecastUIState.collectAsState()
    val isThereAnyRainToday = forecastUIState.isThereAnyRainToday

    val notificationTitleText =
    if (isThereAnyRainToday == true) {
        "It's going to rain today"
    } else {
        "No rain today"
    }
    val notificationRainText =
    if (isThereAnyRainToday == true) {
        "Take an umbrella with you."
    } else {
        "No need for an umbrella today."
    }

    val context = LocalContext.current

    var hasNotificationPermission by remember {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                )
            } else {
                TODO("VERSION.SDK_INT < TIRAMISU")
            }
    }

    val permissionLauncher = rememberLauncherForActivityResult(

        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )

    Button(onClick = {
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }) {
        Text("Request notification permission")
    }
    Button(onClick = {

        if (hasNotificationPermission) {
            showNotification(notificationRainText, notificationTitleText, context)
        }

    }) {

        Text("Show notification")
    }

}


@SuppressLint("ScheduleExactAlarm")
private fun showNotification(notificationRainText : String, notificationTitleText : String, context: Context) {

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.heavyrain)
    val notification = NotificationCompat.Builder(context.applicationContext, "forecast")
        .setContentTitle(notificationTitleText)
        .setContentText(notificationRainText)
        .setSmallIcon(R.drawable.heavyrain)
        .setLargeIcon(bitmap)
        .build()
    notificationManager.notify(2, notification)
}

