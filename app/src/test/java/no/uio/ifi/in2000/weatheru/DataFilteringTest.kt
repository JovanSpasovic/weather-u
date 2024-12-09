package no.uio.ifi.in2000.weatheru

import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.weatheru.data.alerts.AlertsRepo
import no.uio.ifi.in2000.weatheru.data.alerts.deserializeAlerts
import no.uio.ifi.in2000.weatheru.data.forecast.ForecastRepo
import no.uio.ifi.in2000.weatheru.model.forecast.LocationForecast
import org.junit.Test
import org.junit.jupiter.api.Assertions

class DataFilteringTest {

    private val forecastRepo = ForecastRepo()
    private val alertsRepo = AlertsRepo()


    //ALERTS TESTS
    @Test
    fun testGetAlertDataNotNull() = runTest {
        //Arrange

        //Act
        val alerts = deserializeAlerts()

        //Assert
        TestCase.assertNotNull(alerts)
    }

    @Test
    fun testGetLanguage() = runTest {
        //Arrange

        val alerts = deserializeAlerts()


        //Act
        val lang: String = alerts?.lang ?: ""

        //Assert
        TestCase.assertEquals("Expected String \"no\" but found $lang.", "no", lang)
    }

    @Test
    fun testGetAreaDescription() = runTest {
        //Arrange
        // alertsRepo()

        //Act
        val areaDescription: String = alertsRepo.getAreaDescription(mockDeserializedAlerts.features[0])

        //Assert
        TestCase.assertEquals("A3", areaDescription)
    }

    @Test
    fun testGetInstruction() = runTest {
        //Arrange
        // alertsRepo()

        //Act
        val instruction: String = alertsRepo.getCurrentInstruction(mockDeserializedAlerts.features[0])
        //Assert
        TestCase.assertEquals("Fjern is raskt fra båten.", instruction)
    }

    @Test
    fun testGetDescription() = runTest {
        //Arrange
        //alertsRepo()

        //Act
        val description: String = alertsRepo.getAreaDescription(mockDeserializedAlerts.features[0])

        //Assert
        TestCase.assertEquals("A3", description)
    }



    //FORECAST TESTS

    @Test
    fun testGetForecastDataNotNull() = runTest {
        //Arrange
        val longitude = 10.1
        val latitude = 59.2

        //Act

        val deserializedForecast: LocationForecast? = forecastRepo.getForecastDeserialized(longitude, latitude)

        //Assert
        TestCase.assertNotNull(deserializedForecast)
    }

    @Test
    fun testGetTemperatureUnit() = runTest {
        //Arrange
        val longitude = 10.1
        val latitude = 59.2

        //Act
        val deserializedForecast: LocationForecast? = forecastRepo.getForecastDeserialized(longitude, latitude)

        TestCase.assertNotNull(deserializedForecast)
        val temp: String = forecastRepo.getTemperatureUnit(deserializedForecast!!)

        //Assert
        TestCase.assertEquals("Expected string \"celsius\", found $temp.", "celsius", temp)
    }

    @Test
    fun testGetPrecipitationUnit() = runTest {
        //Arrange
        val longitude = 10.1
        val latitude = 59.2

        //Act
        val deserializedForecast: LocationForecast? = forecastRepo.getForecastDeserialized(longitude, latitude)

        TestCase.assertNotNull(deserializedForecast)
        val precipitation: String = forecastRepo.getRainUnit(deserializedForecast!!)

        //Assert
        TestCase.assertEquals("Expected string \"mm\", found $precipitation.", "mm", precipitation)
    }

    @Test
    fun testGetCloudAreaFractionUnit() = runTest {
        //Arrange
        val longitude = 10.1
        val latitude = 59.2

        //Act
        val deserializedForecast: LocationForecast? = forecastRepo.getForecastDeserialized(longitude, latitude)
        TestCase.assertNotNull(deserializedForecast)

        val cloudAreaFraction: String = forecastRepo.getCloudAreaFractionUnit(deserializedForecast!!)

        //Assert
        TestCase.assertEquals(
            "Expected string \"%\", found $cloudAreaFraction.",
            "%",
            cloudAreaFraction
        )
    }

    @Test
    fun testGetTemperatureValue() = runTest {
        //Arrange
        //forecastRepo

        //Act
        val temp: Double = forecastRepo.getCurrentTemperature(mockDeserializedForecast)

        //Assert
        TestCase.assertEquals("Expected Double -2.2, found $temp.", -2.2, temp)
    }

    @Test
    fun testGetPrecipitationValue() = runTest {
        //Arrange
        // forecastRepo

        //Act
        val precipitation: Double = forecastRepo.getCurrentPrecipitation(mockDeserializedForecast)

        //Assert
        TestCase.assertEquals(0.0, precipitation)
    }

    @Test
    fun testGetCloudAreaFractionValue() = runTest {
        //Arrange
        // forecastRepo

        //Act
        val cloudedAreaFraction: Double = forecastRepo.getCurrentCloudAreaFraction(mockDeserializedForecast)

        //Assert
        TestCase.assertEquals(
            "Expected Double 67.6, found $cloudedAreaFraction.",
            67.6,
            cloudedAreaFraction
        )
    }

    @Test
    fun testGetDailyMaxTemperaturesForNext7Days() = runTest {
        //  Act
        val longitude = 10.1
        val latitude = 59.2

        //Act
        val deserializedForecast: LocationForecast? = forecastRepo.getForecastDeserialized(longitude, latitude)

        TestCase.assertNotNull(deserializedForecast)
        val maxTemperaturesNext7DaysList = forecastRepo.getDailyMaxTemperaturesForNext7Days(deserializedForecast!!)

        //  Assert
        TestCase.assertEquals(7, maxTemperaturesNext7DaysList.size)

    }

    @Test
    fun testGetDailyMinTemperaturesForNext7Days() = runTest {
        //  Act
        val longitude = 10.1
        val latitude = 59.2

        //Act
        val deserializedForecast: LocationForecast? = forecastRepo.getForecastDeserialized(longitude, latitude)

        TestCase.assertNotNull(deserializedForecast)
        val minTemperaturesNext7DaysList = forecastRepo.getDailyMaxTemperaturesForNext7Days(deserializedForecast!!)

        //  Assert
        TestCase.assertEquals(7, minTemperaturesNext7DaysList.size)

    }


    //RAIN TESTS
    //private val rainRepo = RainRepo()


    @Test
    fun testGetHourlyRainForNext24Hours() = runTest  {
        // Arrange
        val longitude = 9.58
        val latitude = 60.1


        //Act
        val deserializedForecast: LocationForecast? = forecastRepo.getForecastDeserialized(longitude, latitude)

        TestCase.assertNotNull(deserializedForecast)
        val hourlyRainForNext24Hours = forecastRepo.getHourlyRainForNext24Hours(deserializedForecast!!)

        // Assert
        TestCase.assertEquals(24, hourlyRainForNext24Hours.size)
        // add each hour assertion?
    }

    @Test
    fun testGetRainForNext7DaysNotNull() = runTest  {
        // Arrange
        val longitude = 9.58
        val latitude = 60.1

        //Act
        val deserializedForecast: LocationForecast? = forecastRepo.getForecastDeserialized(longitude, latitude)

        TestCase.assertNotNull(deserializedForecast)
        val rainForNext7Days = forecastRepo.getRainForNext7Days(deserializedForecast!!)

        // Assert
        Assertions.assertNotNull(rainForNext7Days)
    }




}