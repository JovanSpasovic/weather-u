package no.uio.ifi.in2000.weatheru.ui.home.cards.temperatureCards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
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
fun TomorrowTemperatureCard(viewModel: HomeScreenViewModel) {
    val forecastUIState by viewModel.forecastUIState.collectAsState()
    val tempTomorrow = forecastUIState.tempTomorrow24h

    tempTomorrow?.take(10)

    ElevatedCard(
        elevation = DefaultCardValues().cardElevation(),
        colors = DefaultCardValues().cardColor(),
        modifier = Modifier
            .height(310.dp)
            //.padding(top = 20.dp, start = 5.dp) //if swipable
            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 5.dp)
            .fillMaxWidth(),
        shape = DefaultCardValues().cardCorners(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth()
        ){
            Text(
                text = "🍃 I morgen",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tempTomorrow?.take(5)?.forEach { (time, temp, weather) ->
                    item{
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(15.dp).width(IntrinsicSize.Max)
                        ) {
                            Text(
                                text = time,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Image(
                                painter = painterResource(getDrawableFromName(name = weather)),
                                contentDescription = "vær ikoner",
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
            HorizontalDivider(
                modifier = Modifier.padding(1.dp),
                thickness = 1.dp
            )
            LazyRow(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tempTomorrow?.drop(5)?.take(5)?.forEach { (time, temp, weather) ->
                    item{
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
                                contentDescription = "vær ikoner",
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
}

