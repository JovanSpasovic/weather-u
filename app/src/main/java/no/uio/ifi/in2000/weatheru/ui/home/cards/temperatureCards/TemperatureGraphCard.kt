package no.uio.ifi.in2000.weatheru.ui.home.cards.temperatureCards

//noinspection UsingMaterialAndMaterial3Libraries


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
import no.uio.ifi.in2000.weatheru.ui.home.cards.DefaultCardValues
import java.util.Locale


@Composable
fun TemperatureGraphCard(viewModel: HomeScreenViewModel) {
    val forecastUIState by viewModel.forecastUIState.collectAsState()
    val temp7Hours = forecastUIState.temp7Hours

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
            ){
                Image(
                    painter = painterResource(getDrawableFromName(name = "termometer")),
                    contentDescription = "termometer 2",
                    modifier = Modifier
                        .size(30.dp)
                )
                Text(
                    text = " Temperatur neste 7 timer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            ) {
                temp7Hours?.let { list ->
                    val entries = list.mapIndexed { index, (_, temp, _) ->
                        Entry(index.toFloat(), temp.toFloat())
                    }
                    val lineDataSet = LineDataSet(entries, "(°C)")
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
                    list.map { it.first }
                    TemperatureGraphCardChart(
                        modifier = Modifier.fillMaxSize(),
                        data = LineData(lineDataSet),
                        isDarkMode = isDarkMode
                    )
                }
            }
        }
    }
}



@Composable
fun TemperatureGraphCardChart(modifier: Modifier, data: LineData, isDarkMode: Boolean) {
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

                legend.isEnabled = true
                legend.textColor = if (isDarkMode) Color.White.toArgb() else Color.Black.toArgb()
            }
        },
        modifier = modifier.padding(10.dp)
    )
}
