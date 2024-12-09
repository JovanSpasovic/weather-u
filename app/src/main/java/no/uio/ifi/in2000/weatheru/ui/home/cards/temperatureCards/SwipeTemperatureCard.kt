package no.uio.ifi.in2000.weatheru.ui.home.cards.temperatureCards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.home.cards.DefaultCardValues


@Composable
fun SwipeTemperatureCard(viewModel: HomeScreenViewModel) {
    val forecastUIState by viewModel.forecastUIState.collectAsState()
    val temp10Hours = forecastUIState.temp10Hours

    // Combine both sections into a single list
    val combinedList = temp10Hours?.take(10) ?: emptyList()

    ElevatedCard(
        elevation = DefaultCardValues().cardElevation(),
        colors = DefaultCardValues().cardColor(),
        modifier = Modifier
            .padding(top = 20.dp, end = 20.dp, start = 20.dp, bottom = 5.dp)
            .fillMaxWidth(),
        shape = DefaultCardValues().cardCorners(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
        ){
            Text(
                text = "🍃 I dag",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),

                ) {
                items(combinedList) { (time, temp, weather) ->
                    //Skeleton of a single data entry
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(15.dp)
                    ) {
                        Text(
                            text = time,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 5.dp)
                        )

                        Image(
                            painter = painterResource(getDrawableFromName(name = weather)), // Load the SVG from the assets folder
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                        val roundedtemp = temp.toInt()
                        Text(
                            text = "$roundedtemp °C",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                }
            }
        }
    }
}
