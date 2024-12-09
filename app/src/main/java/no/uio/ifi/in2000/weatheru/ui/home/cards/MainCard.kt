package no.uio.ifi.in2000.weatheru.ui.home.cards


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.home.cards.temperatureCards.getDrawableFromName

@SuppressLint("SuspiciousIndentation")
@Composable
fun MainCard(viewModel: HomeScreenViewModel) {
    val forecastUIState by viewModel.forecastUIState.collectAsState()

    val currentWindSpeed = forecastUIState.currentWindSpeed
    val currentTemperature = forecastUIState.currentTemperature
    val sunrise = forecastUIState.sunrise
    val sunset = forecastUIState.sunset


    ElevatedCard(
        elevation = DefaultCardValues().cardElevation(),
        colors = DefaultCardValues().cardColor(),
        modifier = Modifier
            .padding(top = 20.dp)
            .padding(start = 20.dp, end = 20.dp, bottom = 5.dp)
            .fillMaxWidth()
            .height(290.dp),
        shape = DefaultCardValues().cardCorners(),
    ) {
        Column(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
                .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⛅️ Akkurat nå",
                fontSize = 18.sp,
                fontWeight = Bold,
                textAlign = TextAlign.Center,
            )
            Row(
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                forecastUIState.currentSymbolCode?.let { getDrawableFromName(name = it) }
                    ?.let { painterResource(it) }?.let {
                        Image(
                            painter = it, // Load the SVG from the assets folder
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(start = 6.dp, end = 2.dp)
                        )
                    }

                currentTemperature?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${currentTemperature?.toInt()}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 69.sp,
                            modifier = Modifier.padding(start = 6.dp, end = 2.dp)
                        )
                        Text(
                            text = "°C",
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 60.sp,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    }
                }
            }
            currentWindSpeed?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = "Vindstyrke:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = Bold,
                        modifier = Modifier.padding(2.dp)
                    )
                    Text(
                        text = "$currentWindSpeed m/s",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                if (sunrise == null || sunset == null) {
                    Column(
                        modifier = Modifier.padding(
                            top = 4.dp,
                            start = 2.dp,
                            end = 8.dp,
                            bottom = 2.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Tidene for soloppgang og solnedgang ble dessverre ikke funnet.",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = Bold,
                                modifier = Modifier.padding(2.dp)
                            )

                        }
                    }

                } else {
                    Column(
                        modifier = Modifier.padding(
                            top = 4.dp,
                            start = 2.dp,
                            end = 8.dp,
                            bottom = 2.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Sol opp: ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = Bold,
                                modifier = Modifier.padding(2.dp)
                            )
                            Text(
                                text = sunrise,
                                style = MaterialTheme.typography.bodyLarge
                            )

                        }
                    }
                    Column(
                        modifier = Modifier.padding(
                            top = 4.dp,
                            start = 8.dp,
                            end = 2.dp,
                            bottom = 2.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Sol ned: ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = Bold,
                                modifier = Modifier.padding(2.dp)
                            )
                            Text(
                                text = sunset,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                }
            }
        }
    }
