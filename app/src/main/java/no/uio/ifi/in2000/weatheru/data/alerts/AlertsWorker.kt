package no.uio.ifi.in2000.weatheru.data.alerts

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
import kotlinx.coroutines.flow.first
import no.uio.ifi.in2000.weatheru.MainActivity
import no.uio.ifi.in2000.weatheru.R
import no.uio.ifi.in2000.weatheru.data.forecast.ForecastWorker
import no.uio.ifi.in2000.weatheru.model.ApiDatabase
import no.uio.ifi.in2000.weatheru.model.alerts.AlertsDataState
import no.uio.ifi.in2000.weatheru.ui.location.Location
import no.uio.ifi.in2000.weatheru.util.AppStateTracker.isAppInForeground
import no.uio.ifi.in2000.weatheru.util.isNetworkAvailable
import java.io.IOException

class AlertsWorker(appContext: Context, workerParams: WorkerParameters):
        CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {

        try {
            if (!isNetworkAvailable(applicationContext)) {
                return Result.retry()
            }

            val alertsRepo = AlertsRepo()
            val databaseInstance = ApiDatabase.getDatabase(applicationContext).alertStateDao()
            val id = inputData.getInt("id", 3) //   Default set to current location database
            if (id == 3) {
                Log.e("AlertsWorker", "Wrong ID from Input data")
                return Result.failure()
            }
            val coordinates = getCoordinates() ?: return Result.failure()


            val latitude = coordinates.latitude
            val longitude = coordinates.longitude

            val dataDeserialized = alertsRepo.getAlertsDeserialized()
            if (dataDeserialized == null) {
                Log.e("AlertsWorker", "Failed to deserialize alerts API")
                return Result.failure()
            }
            val lastChanged: String = alertsRepo.getLastChange(dataDeserialized)

            //  Returns Features object and assigns necessary info from API if alert for area is found
            alertsRepo.getAlertIfDangerExistsInArea(dataDeserialized, latitude, longitude)
                ?.let { getAlertsInArea ->
                    Log.d("AlertsWorker", "Currently inside getAlertIfDanger...()")
                    //  Collects alerts-values from deserialized object
                    val getEventAwarenessName = alertsRepo.getCurrentEventAwarenessName(getAlertsInArea)
                    val getEvent = alertsRepo.getEvent(getAlertsInArea)
                    val getEventEndingTime = alertsRepo.getEventEndingTime(getAlertsInArea)
                    val getCurrentDescription = alertsRepo.getCurrentDescription(getAlertsInArea)
                    val getCurrentInstruction = alertsRepo.getCurrentInstruction(getAlertsInArea)
                    val getCurrentConsequences = alertsRepo.getCurrentConsequences(getAlertsInArea)
                    val getMatrixColor = alertsRepo.getCurrentRiskMatrixColors(getAlertsInArea)

                    //  Saves values in AlertsState object
                    val alertsState = AlertsDataState(
                        eventAwarenessName = getEventAwarenessName,
                        consequences = getCurrentConsequences,
                        description = getCurrentDescription,
                        instruction = getCurrentInstruction,
                        event = getEvent,
                        eventEndingTime = getEventEndingTime,
                        matrixColor = getMatrixColor,
                        lastChange = lastChanged,
                        id = id
                    )

                    /*
                    if (valueOfPreviousLastChange != alertsState.lastChange) {
                        //  Inserts new values from alertsState into room database
                        databaseInstance.insert(alertsState)
                        databaseInstance.update(alertsState)
                    }
                    delay(2000)
                    Log.d("AlertsWorker", "Inside Alert-area!: ${alertsState}")
                    Log.d("AlertsWorker", "Inside Alert-area!database: ${databaseInstance.getLatestAlertState().first()}")
                     */


                    //  Shows danger alert only if not currently using current location in homeScreen in app
                    databaseInstance.insert(alertsState)
                    if (!isAppInForeground) {
                        val databaseInstance = ApiDatabase.getDatabase(applicationContext).alertStateDao()
                        showNotification(databaseInstance.getLatestAlertState(id).first())
                    }

                    return Result.success()
                }

            Log.d("AlertsWorker", "Currently outside getAlertIfDanger...()")

            //  If an alert is no longer found in current location, the data is removed
            //  such that no alerts are shown to the user
            val alertsState = AlertsDataState(
                eventAwarenessName = "",
                consequences = "",
                description = "",
                instruction = "",
                event = "",
                eventEndingTime = "",
                matrixColor = "",
                lastChange = "",
                id = id
            )

            /*
            if (valueOfPreviousLastChange != alertsState.lastChange) {
                //  Inserts new values from alertsState into room database
            }
            Log.d("AlertsWorker", "previous last change: ${valueOfPreviousLastChange}")
            delay(2000)
            Log.d("AlertsWorker", "data class value: ${alertsState}")
            Log.d("AlertsWorker", "this is value: ${databaseInstance.getLatestAlertState().first()}")
             */

            databaseInstance.insert(alertsState)

        } catch (e: IOException) {
            Log.e("AlertsWorker", "IOException, retrying... ", e)
            return Result.retry()
        } catch (e: Exception) {
            Log.e("AlertsWorker", "An unknown error occurred: ", e)
            return Result.failure()
        }
        return Result.success()
    }





    private fun showNotification(alertsState: AlertsDataState?) {
        if (alertsState == null) {return}
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val channel = NotificationChannel("alerts_channel", "Alerts", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)


        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_warning_generic_yellow)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // Build the notification
        val notification = NotificationCompat.Builder(applicationContext, "alerts_channel")
            .setContentTitle("Warning: ${alertsState.eventAwarenessName}")
            .setContentText(alertsState.description)
            .setSmallIcon(R.drawable.danger)
            .setLargeIcon(bitmap)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show the notification
        notificationManager.notify(1, notification)
    }

    private fun getCoordinates(): Location? {
        Log.d("AlertsWorker", "Tries to convert from Data to coordinates:")
        val latitude = inputData.getDouble("latitude", ForecastWorker.INVALID_DOUBLE)
        val longitude = inputData.getDouble("longitude", ForecastWorker.INVALID_DOUBLE)

        if (latitude == 0.0 || longitude == 0.0) {
            Log.e("AlertsWorker", "Something went wrong for coordinates. Got 0.0, so values were likely not found.")
        } else if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
            Log.e("AlertsWorker", "Coordinates out of range: Latitude: $latitude, Longitude: $longitude")
        }

        if (latitude == ForecastWorker.INVALID_DOUBLE || longitude == ForecastWorker.INVALID_DOUBLE) {
            Log.e("AlertsWorker", "Exception: Database could not access given location coordinates")
            return null
        }

        return Location(
            latitude = latitude,
            longitude = longitude
        )
    }

}