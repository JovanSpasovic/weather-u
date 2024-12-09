package no.uio.ifi.in2000.weatheru.data.forecast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.weatheru.MainActivity
import no.uio.ifi.in2000.weatheru.R
import no.uio.ifi.in2000.weatheru.model.ApiDatabase
import no.uio.ifi.in2000.weatheru.ui.location.Location
import no.uio.ifi.in2000.weatheru.util.isNetworkAvailable

class RainReminderWorker (appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {

        //  Check if internet is available
        val networkIsAvailable: Boolean = isNetworkAvailable(applicationContext)

        //  Do rain check based on network availability
        val isThereAnyRainToday = if (networkIsAvailable) {
            checkRainStatusOnline()
        } else {
            checkRainStatusOffline(applicationContext)
        } ?: throw Exception("Error in checking rain status")

        //  Make notification text based on rain status for today
        val notificationTitleText =
            if (isThereAnyRainToday) {
                "Det kommer til å regne idag" //"It's going to rain today"
            } else "Ingen regn idag" //"No rain today"
        val notificationRainText =
            if (isThereAnyRainToday) {
                "Ta med deg en paraply!!" //"Take an umbrella with you!"
            } else "Ingen behov for paraply!!" //"No need for an umbrella today!"

        showNotification(notificationRainText, notificationTitleText, applicationContext)
        return Result.success()
    }


    private fun showNotification(
        notificationRainText: String,
        notificationTitleText: String,
        context: Context
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val channelName = "Weather Forecasts"
        val channelDescription = "Notifications for weather forecasts"
        val channel = NotificationChannel(
            "forecast_channel",
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = channelDescription
            }
        notificationManager.createNotificationChannel(channel)


        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.heavyrain)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, "forecast_channel")
            .setContentTitle(notificationTitleText)
            .setContentText(notificationRainText)
            .setSmallIcon(R.drawable.heavyrain)
            .setLargeIcon(bitmap)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(2, notification)
    }


    private fun getCoordinates(): Location {
        Log.d("RainReminderWorker", "Tries to convert from Data to coordinates:")
        val latitude = inputData.getDouble("latitude", ForecastWorker.INVALID_DOUBLE)
        val longitude = inputData.getDouble("longitude", ForecastWorker.INVALID_DOUBLE)
        if (latitude == 0.0 || longitude == 0.0) {
            Log.e(
                "RainReminderWorker",
                "Something went wrong for coordinates. Got 0.0, so values were likely not found."
            )
        } else if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
            Log.e(
                "RainReminderWorker",
                "Coordinates out of range: Latitude: $latitude, Longitude: $longitude"
            )
        }

        if (latitude == ForecastWorker.INVALID_DOUBLE || longitude == ForecastWorker.INVALID_DOUBLE) {
            Log.e(
                "RainReminderWorker",
                "Exception: Database could not access given location coordinates"
            )
            return Location()
        }

        return Location(latitude, longitude)
    }

    private suspend fun checkRainStatusOnline(): Boolean? {
        return runCatching {
            val forecastRepo = ForecastRepo()
            val coordinates = getCoordinates()
            val dataDeserialized =
                forecastRepo.getForecastDeserialized(coordinates.latitude, coordinates.longitude)
                    ?: return null

            forecastRepo.isThereAnyRainToday(dataDeserialized)
        }.getOrElse {
            Log.e("RainReminderWorker", "Online check error: ${it.message}")
            null
        }
    }

    private suspend fun checkRainStatusOffline(applicationContext: Context): Boolean? {
        return runCatching {
            withContext(Dispatchers.IO) {
                val forecastDatabaseInstance =
                    ApiDatabase.getDatabase(applicationContext).forecastStateDao()
                val isThereAnyRainToday = forecastDatabaseInstance.getRainInfoForToday(0)
                isThereAnyRainToday
            }
        }.getOrElse {
            Log.e("RainReminderWorker", "Offline check error: ${it.message}")
            null
        }
    }
}