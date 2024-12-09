package no.uio.ifi.in2000.weatheru.data

import android.app.Application

class ApplicationClass : Application() {

    lateinit var apiManager: ApiManager

    override fun onCreate() {
        super.onCreate()
        apiManager = ApiManager(applicationContext)
        apiManager.initialize()
    }
}