
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.home.cards.DefaultCardValues
import no.uio.ifi.in2000.weatheru.ui.home.cards.temperatureCards.getDrawableFromName


@Composable
fun WindCard(viewModel: HomeScreenViewModel) {

    val forecastUIState by viewModel.forecastUIState.collectAsState()
    val currentWindSpeed = forecastUIState.currentWindSpeed
    val currentWindFromDirection = forecastUIState.currentWindFromDirection

    ElevatedCard(
        elevation = DefaultCardValues().cardElevation(),
        colors = DefaultCardValues().cardColor(),
        modifier = Modifier
            .padding(top = 20.dp, end = 20.dp, start = 20.dp, bottom = 5.dp)
            .fillMaxWidth(),
        shape = DefaultCardValues().cardCorners(),
        ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp)
            ) {
                Image(
                    //Needs to be changed!!!
                    painter = painterResource(getDrawableFromName(name = "wind_icon")),
                    contentDescription = "Vind Icon",
                    modifier = Modifier.size(30.dp),
                )

                Text(
                    text = "Wind",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(10.dp)
                )
            }


                if (currentWindSpeed != null && currentWindFromDirection != null) {
                    Text(
                        text = "Vindkast: $currentWindSpeed m/s",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 10.dp, start = 10.dp, bottom = 10.dp)
                    )

                    Text(
                        text = "Vind retning: ${convertWindDirection(currentWindFromDirection)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                    )
                } else {
                    Text(
                        //text = "Wind information not available",
                        text = "", //"Vind informasjon er ikke tilgjengelig.",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(10.dp)
                    )
                }
        }
    }
}



@Composable
fun convertWindDirection(degrees: Double): String {
    return when {
        degrees >= 337.5 || degrees < 22.5 -> "Sør"
        degrees >= 22.5 && degrees < 67.5 -> "Sørvest"
        degrees >= 67.5 && degrees < 112.5 -> "Vest"
        degrees >= 112.5 && degrees < 157.5 -> "Nordvest"
        degrees >= 157.5 && degrees < 202.5 -> "Nord"
        degrees >= 202.5 && degrees < 247.5 -> "Nordøst"
        degrees >= 247.5 && degrees < 292.5 -> "Øst"
        else -> "Southeast"
    }
}
