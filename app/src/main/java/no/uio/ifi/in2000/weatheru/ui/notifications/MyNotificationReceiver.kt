package no.uio.ifi.in2000.weatheru.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Retrieve notification title and text from intent extras
        intent?.getStringExtra("notificationTitle")
        intent?.getStringExtra("notificationText")

    }
}