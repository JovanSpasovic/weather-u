package no.uio.ifi.in2000.weatheru

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.weatheru.data.sunrise.SunriseRepo
import org.junit.Test


class SunriseTest {

    // All the tests will not succeed if you run all the tests at once. You have to run them one by one.
    private val longitude = 10.716667
    private val latitude = 59.916667


    @Test
    fun testSunriseCopyrightName() = runTest {
        val sunrise = SunriseRepo().deserializeSunriseApi(longitude, latitude)
        assertEquals("MET Norway", sunrise?.copyright)
    }

    @Test
    fun testGetSunrise() = runTest {

        val sunriseApi = SunriseRepo().deserializeSunriseApi(longitude, latitude)

        val sunriseValue = sunriseApi?.properties?.sunrise?.time
        assertNotNull(sunriseValue)
    }

    @Test
    fun testGetSunset() = runTest {

        val sunriseApi = SunriseRepo().deserializeSunriseApi(longitude, latitude)

        val sunsetValue = sunriseApi?.properties?.sunset?.time
        assertNotNull(sunsetValue)
    }

}