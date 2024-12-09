package no.uio.ifi.in2000.weatheru.util

object AppStateTracker {
    var isAppInForeground: Boolean = false
        private set                         //  Used so that isAppInForeground can be
                                            //  globally observed

    fun updateAppState(currentAppState: Boolean) {
        isAppInForeground = currentAppState
    }
}