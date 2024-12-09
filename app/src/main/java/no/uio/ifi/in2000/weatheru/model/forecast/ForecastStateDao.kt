package no.uio.ifi.in2000.weatheru.model.forecast

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000.weatheru.model.nominatim.Address
import no.uio.ifi.in2000.weatheru.ui.location.Location


//  ID of 0 indicates current location, while an
//  ID of 1 indicates any other location
@Dao
interface ForecastStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(forecastDataState: ForecastDataState)

    @Query("SELECT * FROM ForecastStateView WHERE id = :id")
    fun getLatestForecastState(id: Int): Flow<ForecastStateView>

    @Query("SELECT lastChange FROM forecast_states WHERE id = :id")
    fun getLastChangeInfo(id: Int): String

    @Query("SELECT locationForecast FROM forecast_states WHERE id = :id")
    suspend fun getLocationForecast(id: Int): LocationForecast?

    @Query("SELECT locationPair FROM forecast_states WHERE id = :id")
    suspend fun getLocation(id: Int): Location?

    @Query("SELECT isThereAnyRainToday FROM forecast_states WHERE id = :id")
    suspend fun getRainInfoForToday(id: Int): Boolean?

    @Query("SELECT timeZone FROM forecast_states WHERE id = :id")
    suspend fun getTimeZone(id: Int): String?

    @Query("SELECT timeZone FROM forecast_states WHERE id = :id")
    fun getTimeZoneFlow(id: Int): Flow<String?>

    @Query("SELECT displayName FROM forecast_states WHERE id = :id")
    suspend fun getDisplayName(id: Int): String?

    @Query("SELECT address FROM forecast_states WHERE id = :id")
    suspend fun getAddress(id: Int): Address?

    @Query("SELECT country FROM forecast_states WHERE id = :id")
    suspend fun getCountry(id: Int): String?


    @Query("SELECT sunrise FROM forecast_states WHERE id = :id")
    suspend fun getSunrise(id: Int): String?

    @Query("SELECT sunset FROM forecast_states WHERE id = :id")
    suspend fun getSunset(id: Int): String?


}