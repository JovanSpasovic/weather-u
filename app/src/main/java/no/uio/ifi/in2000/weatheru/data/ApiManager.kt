package no.uio.ifi.in2000.weatheru.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.room.Room
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.github.mikephil.charting.utils.Utils
import no.uio.ifi.in2000.weatheru.data.alerts.AlertsWorker
import no.uio.ifi.in2000.weatheru.data.forecast.ForecastWorker
import no.uio.ifi.in2000.weatheru.data.forecast.RainReminderWorker
import no.uio.ifi.in2000.weatheru.model.ApiDatabase
import no.uio.ifi.in2000.weatheru.util.AppLifecycleObserver
import no.uio.ifi.in2000.weatheru.util.getDelayUntilTimeNextDay
import java.util.concurrent.TimeUnit

class ApiManager(private val context: Context) : LifecycleObserver {
    private lateinit var db: ApiDatabase
    fun initialize() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver()) //  May be used in viewModels to avoid
        //  memory leaks and be sure that the component you are trying to get is in a valid state
        db = Room.databaseBuilder(context, ApiDatabase::class.java, "Api_database")
            .fallbackToDestructiveMigration() // Or addMigrations() for other migration strategy
            .build()
        Utils.init(context)
    }

    fun getApplicationContext() : Context {
        return context
    }


    //  Rain notification that is scheduled to every morning
    fun startRainNotifications(lat: Double, long: Double, timeZone: String?, hour : Int = 6, minute: Int = 0) {
        Log.d("ApiManager", "Currently inside startRainNotifications")

        //  Stop work early if no timeZone value is given
        if (timeZone == "" || timeZone == null) {
            Log.e("ApiManager", "TimeZone value is empty. Rain notifications will not be started.")
            return
        }

        val data = Data.Builder()
            .putDouble("latitude", lat)
            .putDouble("longitude", long)
            .build()

        val delay = getDelayUntilTimeNextDay(hour, minute, timeZone)
        if (delay > 0) { // Ensure the delay is positive - target time is in the future. Should be a useless check
            val workRequest = PeriodicWorkRequestBuilder<RainReminderWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "rainNotificationWorker",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest)
        }
    }


    fun cancelRainNotifications() {
        Log.d("ApiManager", "cancelLocationAlert:\nCancelling unique work for rain notifications:")

        //  Cancel worker
        WorkManager.getInstance(context).cancelUniqueWork("rainNotificationWorker")
    }


    fun updateForecastOnce(lat: Double, long: Double, id: Int) {
        Log.d("ApiManager", "Currently inside updateForecastOnce")

        //  Tries to update the room database with new forecast values from
        //  either API if internet is available or database-cache if not.
        val data = Data.Builder()
            .putDouble("latitude", lat)
            .putDouble("longitude", long)
            .putInt("id", id)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ForecastWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "updateForecastOnce",
            ExistingWorkPolicy.REPLACE,  // This will cancel existing work and replace it
            workRequest
        )
    }


    fun updateCurrentForecastOnce(lat: Double, long: Double, id: Int) {
        Log.d("ApiManager", "Currently inside updateCurrentForecastOnce")

        //  Tries to update the room database with new forecast values from
        //  either API if internet is available or database-cache if not.
        val data = Data.Builder()
            .putDouble("latitude", lat)
            .putDouble("longitude", long)
            .putInt("id", id)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ForecastWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "updateCurrentForecastOnce",
            ExistingWorkPolicy.REPLACE,  // This will cancel existing work and replace it
            workRequest
        )
    }


    fun updateCurrentAlertsContinually(lat: Double, long: Double, id: Int) {
        Log.d("ApiManager", "Currently inside updateContinuousAlertsData")

        val data = Data.Builder()
            .putDouble("latitude", lat)
            .putDouble("longitude", long)
            .putInt("id", id)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<AlertsWorker>(15, TimeUnit.MINUTES)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR, // LINEAR or EXPONENTIAL, where exponential doubles the value each iteration
                30, //WorkRequest.MIN_BACKOFF_MILLIS is 10 seconds, but a google issue tracker recommended using min = 30s
                TimeUnit.SECONDS
            )
            .setInputData(data)
            .build()


        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "checkingAlertsPeriodically",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }



    fun updateAlertsOnce(lat: Double, long: Double, id: Int) {
        Log.d("ApiManager", "Currently inside updateAlertsOnce")

        val data = Data.Builder()
            .putDouble("latitude", lat)
            .putDouble("longitude", long)
            .putInt("id", id)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<AlertsWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}