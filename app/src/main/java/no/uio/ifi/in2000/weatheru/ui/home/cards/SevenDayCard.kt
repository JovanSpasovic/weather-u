package no.uio.ifi.in2000.weatheru.ui.home.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.weatheru.R
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.home.cards.temperatureCards.getDrawableFromName
import kotlin.math.roundToInt

/**
 * Creates a card that displays the weather forecast for the next four days.
 * The card uses columns to display the data, temperature, weather icon, and date.
 * The weather icon is displayed in a separate column, while the temperature and date are displayed in another column.
 */
@Composable
fun SevenDayCard(viewModel: HomeScreenViewModel) {
    val forecastUIState by viewModel.forecastUIState.collectAsState()
    val maxTemperature7xDays = forecastUIState.maxtemperature7xdays?.map { it.roundToInt() }
    val minTemperatures7xDays = forecastUIState.mintemperatures7xdays?.map { it.roundToInt() }
    val dailyDateAndCloudInfo = forecastUIState.getDailyDateAndCloudInfo


    ElevatedCard(
        elevation = DefaultCardValues().cardElevation(),
        colors = DefaultCardValues().cardColor(),
        modifier = Modifier
            .height(IntrinsicSize.Max) // Increase the height to accommodate more content
            .padding(top = 20.dp, end = 20.dp, start = 20.dp, bottom = 5.dp)
            .fillMaxWidth(),
        shape = DefaultCardValues().cardCorners(),
    ) {
        Column(
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kalender),
                    contentDescription = "Calendar Icon",
                    modifier = Modifier.size(24.dp))
                Text(
                    text = " Denne uken",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row( verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp))
            {
                Text(text = "Dag",fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                )

                Text(
                    text = "Hi/Lo",
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                )
            }

            dailyDateAndCloudInfo?.let { info ->
                val daysToShow = info.entries.take(7) // Always show the next 7 days
                daysToShow.forEachIndexed { i, entry ->
                    val date = entry.key
                    val cloudInfo = entry.value
                    val temperatureInfo =
                        "${maxTemperature7xDays?.get(i)}° / ${minTemperatures7xDays?.get(i)}°"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, bottom = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = date,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(0.5f)
                        )

                        Image(
                            painter =painterResource(getDrawableFromName(name = cloudInfo)),
                            contentDescription = "Image",
                            alignment = Alignment.Center,
                            modifier = Modifier.size(30.dp)

                        )

                        Text(
                            text = temperatureInfo,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f), textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}