package no.uio.ifi.in2000.weatheru



import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.weatheru.data.alerts.deserializeAlerts
import no.uio.ifi.in2000.weatheru.model.alerts.CurrentAlerts
import org.junit.Test

class AlertRetrievalTest {


    @Test
    fun testGetAlertDataNotNull() = runTest {
        //Arrange

        //Act
        val alerts = deserializeAlerts()

        //Assert
        assertNotNull(alerts)
    }

    @Test
    fun testGetLanguage() = runTest {
        //Arrange
        val gson = Gson()
        val alert = gson.fromJson(mockAlertJSON, CurrentAlerts::class.java)

        //Act
        val lang: String = alert.lang

        //Assert
        assertEquals("Expected String \"no\" but found $lang.", "no", lang)
    }

    @Test
    fun testGetAreaDescription() = runTest {
        //Arrange
        val gson = Gson()
        val alert = gson.fromJson(mockAlertJSON, CurrentAlerts::class.java)
        //Act
        val areaDescription: String = alert.features[0].properties.area

        //Assert
        assertEquals("A3", areaDescription)
    }

    @Test
    fun testGetAwarenessSeriousness() = runTest {
        //Arrange
        val gson = Gson()
        val alert = gson.fromJson(mockAlertJSON, CurrentAlerts::class.java)
        //Act
        val awarenessSeriousness: String = alert.features[0].properties.awarenessSeriousness

        //Assert
        assertEquals("Alvorlig situasjon", awarenessSeriousness)
    }

    @Test
    fun testGetDescription() = runTest {
        //Arrange
        val gson = Gson()
        val alert = gson.fromJson(mockAlertJSON, CurrentAlerts::class.java)
        //Act
        val description: String = alert.features[0].properties.description

        //Assert
        assertEquals("Utrygt for sterk ising.", description)
    }

    @Test
    fun testGetCeilingAboveSeaLevel() = runTest {
        //Arrange
        val gson = Gson()
        val alert = gson.fromJson(mockAlertJSON, CurrentAlerts::class.java)
        //Act
        val ceilingAboveSeaLevel: Int = alert.features[0].properties.ceiling_above_sea_level

        //Assert
        assertEquals(274, ceilingAboveSeaLevel)
    }



}