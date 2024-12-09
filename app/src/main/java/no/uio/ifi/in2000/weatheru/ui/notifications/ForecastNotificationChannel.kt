package no.uio.ifi.in2000.weatheru.ui.notifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class ForecastNotificationChannel : Application(){


    override fun onCreate(){
        super.onCreate()
        val channel = NotificationChannel(
            "forecast",
            "Forecast",
            NotificationManager.IMPORTANCE_HIGH

        )
        channel.description = "Weather forecast notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}