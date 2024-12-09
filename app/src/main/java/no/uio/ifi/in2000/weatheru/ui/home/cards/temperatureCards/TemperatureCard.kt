package no.uio.ifi.in2000.weatheru.ui.home.cards.temperatureCards

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.home.cards.DefaultCardValues


@SuppressLint("DiscouragedApi")
@Composable
fun getDrawableFromName(name: String): Int {
    return LocalContext.current.resources.getIdentifier(
        name,
        "drawable",
        LocalContext.current.packageName
    )
}


@Composable
fun TemperatureCard(viewModel: HomeScreenViewModel) {
    val forecastUIState by viewModel.forecastUIState.collectAsState()
    val temp10Hours = forecastUIState.temp10Hours

    ElevatedCard(
        elevation = DefaultCardValues().cardElevation(),
        colors = DefaultCardValues().cardColor(),
        modifier = Modifier
            .height(310.dp)
            //.padding(top = 20.dp, start = 5.dp, end = 20.dp) //if swipeable
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
                text = "🍃 I dag",
                fontWeight = Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,

                ) {
                temp10Hours?.take(5)?.forEach { (time, temp, weather) ->
                    item{
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(15.dp).width(IntrinsicSize.Max)
                        ) {
                            Text(
                                text = time,
                                fontWeight = Bold,
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

            //Renders a line between the rows.
            HorizontalDivider(
                modifier = Modifier
                    .padding(1.dp),
                thickness = 1.dp
            )

            LazyRow(
                horizontalArrangement =Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {


                temp10Hours?.drop(5)?.forEach { (time, temp, weather) ->
                    item{

                        //Skeleton of a single data entry
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement =Arrangement.Center,
                            modifier = Modifier.padding(15.dp)
                        ) {
                            Text(
                                text = time,
                                fontWeight = Bold,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )

                            Image(
                                painter = painterResource(getDrawableFromName(name = weather)), // Load the SVG from the assets folder
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)

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


