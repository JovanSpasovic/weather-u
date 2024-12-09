package no.uio.ifi.in2000.weatheru.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        AppStateTracker.updateAppState(true)
    }

    override fun onStop(owner: LifecycleOwner) {
        AppStateTracker.updateAppState(false)
    }
}