package no.uio.ifi.in2000.weatheru.model.alerts

import androidx.room.Entity
import androidx.room.PrimaryKey


//  In addition, it now has the attribute "lastChange". This is to help the worker class differentiate if
//  the data class has been separated or not, so that no unnecessary updates/insertions are done
//  into the table/database.
@Entity(tableName = "alert_states")  // Camelcase is not convention for databases
data class AlertsDataState(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val eventAwarenessName: String = "",
    val description: String = "",
    val consequences: String = "",
    val instruction: String = "",
    val eventEndingTime: String = "",
    val event: String = "",
    val matrixColor: String = "",
    val lastChange: String = ""
)

