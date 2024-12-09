
package no.uio.ifi.in2000.weatheru.ui.home.cards


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel


@Composable
fun AlertCard(viewModel: HomeScreenViewModel){
    val alertUIState by viewModel.alertsUIState.collectAsState()
    val eventAwarenessName = alertUIState.eventAwarenessName
    val description = alertUIState.description
    val event = alertUIState.event
    val consequences = alertUIState.consequences
    val instruction = alertUIState.instruction
    val eventEndingTime = alertUIState.eventEndingTime
    val matrixColor: String = alertUIState.matrixColor

    val isAnyPropertyNull: Boolean = listOf(
            alertUIState.eventAwarenessName,
            alertUIState.description,
            alertUIState.event,
            alertUIState.consequences,
            alertUIState.instruction,
            alertUIState.eventEndingTime,
            alertUIState.matrixColor
    ).any { it == "" }

    // If any property is null, do not make the Alert-Card
    if (isAnyPropertyNull) return

    var expanded by remember { mutableStateOf (false) }


    ElevatedCard(
        elevation = DefaultCardValues().cardElevation(),
        colors = DefaultCardValues().alertCardColor(matrixColor),
        modifier = Modifier
            .padding(top = 20.dp, end = 20.dp, start = 20.dp, bottom = 5.dp)
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { expanded = !expanded },
        shape = DefaultCardValues().cardCorners()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                //.background(getMatrixColorToComposeColor(matrixColor)) // Remove slashes to add adaptive color to the card
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .weight(0.2f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(
                            getDrawableFromNameAlert(name = getIconStringFromAwarenessLevelAndEvent(matrixColor, event))
                        ),
                        contentDescription = "Danger icon",
                        modifier = Modifier.size(50.dp),
                        alignment = Alignment.Center
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .weight(0.8f)
                ) {
                    Text(
                        text = "Farevarsel: ${eventAwarenessName.substringBefore(".")}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = description.substringBefore("."),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(
                        modifier = Modifier
                            .padding(end = 24.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = if (expanded) "Vis mindre" else "Vis mer",
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clickable { expanded = !expanded },
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = eventEndingTime,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
                Text(
                    text = consequences,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
                Text(
                    text = instruction,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun getDrawableFromNameAlert(name: String): Int {
    return LocalContext.current.resources.getIdentifier(
        name, "drawable", LocalContext.current.packageName
    )
}

@Composable
fun getIconStringFromAwarenessLevelAndEvent(matrixColor: String, event: String): String {
    val matrixColorLower = matrixColor.lowercase()
    return when (val eventLower = event.lowercase()) {
        "blowingsnow" -> "icon_warning_snow_$matrixColorLower"
        "gale" -> "icon_warning_wind_$matrixColorLower"
        "icing" -> "icon_warning_generic_$matrixColorLower"
        "avalanches" -> "icon_warning_avalanches_$matrixColorLower"
        "unknown" -> "icon_warning_generic_$matrixColorLower"
        "rainFlood" -> "icon_warning_rainflood_$matrixColorLower"

        else -> "icon_warning_${eventLower}_$matrixColorLower"
    }
}