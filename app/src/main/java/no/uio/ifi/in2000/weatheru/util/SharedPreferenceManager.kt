package no.uio.ifi.in2000.weatheru.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import no.uio.ifi.in2000.weatheru.ui.location.LocationWithName

class SharedPreferencesManager(private val context: Context) {
    //  Location Id
    fun saveLocationId(locationId: Int) {
        val sharedPreferences = context.getSharedPreferences("updateLocationId", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("updateLocationId", locationId).apply()
    }

    fun loadLocationId(defaultValue: Int = CURRENT_LOCATION_DATABASE): Int {
        val sharedPreferences = context.getSharedPreferences("updateLocationId", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("updateLocationId", defaultValue)
    }

    /// Save Location object with name
    fun saveLocationWithName(locationWithName: LocationWithName) {
        val sharedPreferences = context.getSharedPreferences("locationWithName", Context.MODE_PRIVATE)
        val json = Gson().toJson(locationWithName)
        sharedPreferences.edit().putString("locationWithName", json).apply()
    }

    // Load Location Name
    fun loadLocationWithName(): LocationWithName {
        val sharedPreferences = context.getSharedPreferences("locationWithName", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("locationWithName", null)
        return if (json != null) {
            Gson().fromJson(json, LocationWithName::class.java)
        } else {
            LocationWithName()
        }
    }

    //  Location list for LocationScreen
    fun saveLocationList(locationList: List<LocationWithName>) {
        val sharedPreferences = context.getSharedPreferences("locationList", Context.MODE_PRIVATE)
        val json = Gson().toJson(locationList)
        sharedPreferences.edit().putString("locationList", json).apply()
    }

    
    fun loadLocationList(): List<LocationWithName> {
        val sharedPreferences = context.getSharedPreferences("locationList", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("locationList", null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<LocationWithName>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    //  Theme
    fun saveTheme(themeCode: String) {
        val sharedPreferences = context.getSharedPreferences("theme", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("theme", themeCode).apply()
    }

    fun loadTheme(defaultValue: String = "gradient"): String {
        val sharedPreferences = context.getSharedPreferences("theme", Context.MODE_PRIVATE)
        return sharedPreferences.getString("theme", defaultValue) ?: defaultValue
    }

    //  Notification toggle
    fun saveNotificationToggle(bool: Boolean) {
        val sharedPreferences = context.getSharedPreferences("notificationToggle", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("notificationToggle", bool)
        editor.apply()
    }

    fun loadNotificationToggle(): Boolean {
        val sharedPreferences = context.getSharedPreferences("notificationToggle", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("notificationToggle", false)
    }


    companion object {
        const val CURRENT_LOCATION_DATABASE = 0
    }
}
