package no.uio.ifi.in2000.weatheru.ui.home.cards


import android.annotation.SuppressLint
import android.util.Log
import android.widget.TextClock
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel

@SuppressLint("SuspiciousIndentation", "SimpleDateFormat")
@Composable
fun LocationTimeCard(viewModel: HomeScreenViewModel) {
    val forecastUIState by viewModel.forecastUIState.collectAsState()

    val currentDate = forecastUIState.currentDate
    val address = forecastUIState.address
    val country = forecastUIState.country
    val timeZone  = forecastUIState.timeZone
    val isInDarkTheme = isSystemInDarkTheme()

    val locationName = remember(address, country) {
        if (address == null || address.name == "") {
            "$country"
        } else {
            "${address.name},\n $country"
        }
    }


    Card(
        colors =  CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 5.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (locationName.isEmpty()) {
                Text(
                    text = "", //"Could not fetch position",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 36.sp,
                    fontWeight = Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                val fontSize = if (locationName.length <= 15) {
                    48.sp
                } else {
                    32.sp
                }
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = fontSize,
                    fontWeight = Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            AndroidView(

                factory = { context ->
                    TextClock(context).apply {
                        format12Hour = null
                        format24Hour = "HH:mm"
                        Log.d("LocationTimeCard", "Time zone valuee name value: $timeZone")
                        timeZone.let { this.timeZone = timeZone }
                        textSize.let { this.textSize = 40f }
                        setTextColor(if (isInDarkTheme) Color.White.toArgb() else Color.Black.toArgb())
                    }
                },
                modifier = Modifier
                    .padding(5.dp)
                    .background(color = Color.Transparent),
                update = { view ->
                    view.timeZone = timeZone
                    Log.d("LocationTimeCard", "Updated time zone to: $timeZone")
                }
            )
            Spacer(modifier = Modifier.height(5.dp))

            // Date
            if (currentDate != null) {
                Text(
                    text = currentDate,
                    fontSize = 20.sp,
                    fontWeight = Bold,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}