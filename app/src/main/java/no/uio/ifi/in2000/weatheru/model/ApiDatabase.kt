package no.uio.ifi.in2000.weatheru.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import no.uio.ifi.in2000.weatheru.model.alerts.AlertsDataState
import no.uio.ifi.in2000.weatheru.model.alerts.AlertsStateDao
import no.uio.ifi.in2000.weatheru.model.forecast.ForecastDataState
import no.uio.ifi.in2000.weatheru.model.forecast.ForecastStateDao
import no.uio.ifi.in2000.weatheru.model.forecast.ForecastStateView
import no.uio.ifi.in2000.weatheru.util.Converters

@Database(entities = [AlertsDataState::class, ForecastDataState::class], views = [ForecastStateView::class], version = 29, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ApiDatabase : RoomDatabase() {

    abstract fun forecastStateDao(): ForecastStateDao
    abstract fun alertStateDao(): AlertsStateDao

    companion object {
        @Volatile
        private var INSTANCE: ApiDatabase? = null

        fun getDatabase(context: Context): ApiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ApiDatabase::class.java,
                    "Api_database",
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
                instance
            }
        }
    }
}
