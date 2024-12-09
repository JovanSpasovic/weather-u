package no.uio.ifi.in2000.weatheru.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.uio.ifi.in2000.weatheru.data.ApiManager

class HomeScreenViewModelFactory(private val apiManager: ApiManager) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeScreenViewModel(apiManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}