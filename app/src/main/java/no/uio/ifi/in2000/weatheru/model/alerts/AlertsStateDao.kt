package no.uio.ifi.in2000.weatheru.model.alerts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


//  ID of 0 indicates current location, while an
//  ID of 1 indicates any other location
@Dao
interface AlertsStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alertState: AlertsDataState)

    // Only one AlertsDataState is stored because previous
    // data is not import for this use-case
    @Query("SELECT * FROM alert_states WHERE id = :id") // Camelcase is not convention for databases
    fun getLatestAlertState(id: Int): Flow<AlertsDataState>


    @Query("SELECT lastChange FROM alert_states WHERE id = :id")
    fun getLastChangeInfo(id: Int): Flow<String>

}
