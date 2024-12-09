package no.uio.ifi.in2000.weatheru.ui.home.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.home.cards.temperatureCards.getDrawableFromName
import java.util.Locale


@Composable
fun RainCard(viewModel: HomeScreenViewModel) {
    val forecastUIState by viewModel.forecastUIState.collectAsState()
    val currentChanceOfRain = forecastUIState.currentChanceOfRain

    val hourlyRainForNext10Hours = forecastUIState.hourlyRainForNext10Hours
    val isDarkMode = isSystemInDarkTheme()

    ElevatedCard(
        elevation = DefaultCardValues().cardElevation(),
        colors = DefaultCardValues().cardColor(),
        modifier = Modifier
            .padding(top = 20.dp, end = 20.dp, start = 20.dp, bottom = 5.dp)
            .height(290.dp)
            .fillMaxWidth(),
        shape = DefaultCardValues().cardCorners(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(start = 4.dp)
            ) {
                Image(
                    painter = painterResource(getDrawableFromName(name = "rain")),
                    contentDescription = "Nedbør Icon",
                    modifier = Modifier
                        .size(30.dp)
                )

                Text(
                    text = " Nedbør i dag",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            currentChanceOfRain?.let {
                Text(
                    text = "Nedbør akkurat nå: $currentChanceOfRain mm",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            ) {

                hourlyRainForNext10Hours?.let { list ->
                    if (list.all { it == 0.0 }) {
                        // If there is no rain for the next 10 hours, display an icon and a text
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Image(
                                painter = painterResource(getDrawableFromName(name = "no_rain")),
                                contentDescription = "No Rain Icon",
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.padding(6.dp))
                            Text(
                                text = "Det vil ikke regne de neste 10 timene",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    } else {
                        val entries = list.mapIndexed { index, value ->
                            Entry(index.toFloat(), value.toFloat())
                        }
                        val lineDataSet = LineDataSet(entries, "Nedbør (mm)")
                        lineDataSet.color = if (isDarkMode) Color.White.toArgb() else Color.Black.toArgb()
                        lineDataSet.valueTextColor = if (isDarkMode) Color.White.toArgb() else Color.Black.toArgb()
                        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                        lineDataSet.setDrawValues(true)
                        lineDataSet.valueTextSize = 10f
                        lineDataSet.valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return String.format(Locale.getDefault(), "%.1f", value)
                            }
                        }
                        lineDataSet.label = "Nedbør (mm)"
                        lineDataSet.setDrawValues(true)
                        lineDataSet.valueTextColor = if (isDarkMode) Color.White.toArgb() else Color.Black.toArgb()

                        LineChart(
                            modifier = Modifier.fillMaxSize(),
                            data = LineData(lineDataSet),
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun LineChart(modifier: Modifier, data: LineData, isDarkMode: Boolean) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                this.data = data
                this.invalidate()
                axisRight.isEnabled = false
                axisLeft.isEnabled = false
                description.isEnabled = false
                xAxis.isEnabled = true
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.textSize = 9f
                if (isDarkMode) {
                    xAxis.textColor = Color.White.toArgb()
                    axisLeft.textColor = Color.White.toArgb()
                } else {
                    xAxis.textColor = Color.Black.toArgb()
                    axisLeft.textColor = Color.Black.toArgb()
                }
                setDrawGridBackground(false)
                setBackgroundColor(Color.Transparent.toArgb())
                setDrawBorders(false)
                setNoDataTextColor(if (isDarkMode) Color.White.toArgb() else Color.Black.toArgb())

                // Set "mm" as unit and round to the first decimal place
                axisLeft.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return String.format(Locale.getDefault(), "%.1f mm", value)
                    }
                }

                legend.isEnabled = true
                legend.textColor = if (isDarkMode) Color.White.toArgb() else Color.Black.toArgb()
            }
        },
        modifier = modifier.padding(10.dp)
    )
}

















