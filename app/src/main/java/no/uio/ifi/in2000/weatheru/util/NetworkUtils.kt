package no.uio.ifi.in2000.weatheru.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

object NetworkClient {
    val metClient: HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            gson()
        }
        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/")
            headers.appendIfNameAbsent("X-Gravitee-API-Key", "7ff621cc-991a-434e-816b-ce5399f69a75")
        }
    }

    val baseClient: HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            gson()
        }
    }
}


class NetworkMonitor(context: Context) {
    private val _networkStatus = MutableStateFlow(isNetworkAvailable(context))
    val networkStatus: StateFlow<Boolean> get() = _networkStatus

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            _networkStatus.value = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            _networkStatus.value = false
        }
    }

    fun registerNetworkCallback() {
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}

