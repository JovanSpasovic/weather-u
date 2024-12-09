package no.uio.ifi.in2000.weatheru.ui.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.material_theming.montserratFamily
import no.uio.ifi.in2000.weatheru.ui.theme.AppTheme


@Composable
fun AboutScreen(viewModel: HomeScreenViewModel,navController: NavController) {

    Log.d("Settings", "We're in about screen")

    AppTheme {
        val contentColor: Color = MaterialTheme.colorScheme.onSurface
        val font: FontFamily = montserratFamily

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(setBackground(viewModel))
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() })
                {
                    Icon(
                        Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = "Back button",
                        modifier = Modifier.size(40.dp),
                        tint = contentColor
                    )
                }

                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Om appen",
                        fontSize = 24.sp,
                        fontFamily = font,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }

            LazyColumn {
                item {
                    Text(
                        text =  "WeatherU er en værapp som er designet for unge. Målet er å nå ut med farevarsler som ikke alltid er lett tilgjengelige gjennom internasjonale tjenester. \n" +
                                "\n" +
                                "Her får du sanntidsinformasjon direkte fra Meterologisk Institutt, i tillegg til mulighet for å sjekke været opp til syv dager frem i tid. Appen gir deg beskjed dersom det blir ekstremvær der du er, og du kan også skru på egne varslinger for å få beskjed dersom det vil regne i løpet av dagen.\n" +
                                "\n" +
                                "I tillegg tilbyr appen flere vakre fargetemaer som gjør at du kan tilpasse utseendet etter dine ønsker. \n" +
                                "\n" +
                                "Appens hensikt er delvis knyttet opp mot WMOs prosjekt Early Warnings for All, som har som ambisjon at alle skal kunne varsles om naturfarer som kan true liv og verdier. \n" +
                                "\n" +
                                "Teamet bak er en gjeng på seks studenter ved Institutt for Informatikk ved Universitetet i Oslo. Appen er et resultat av et prosjekt i emnet IN2000 Software Engineering våren 2024. " +
                                "\n" +
                                "\n" +
                                "Vi håper at du liker appen like godt som vi har likt å lage den. ❤",

                        fontSize = 12.sp,
                        fontFamily = font,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        HyperlinkText(
                            fullText = "Geokoding data tilbydd av © OpenStreetMap gjennom Open Database License.\n\n\n",
                            linkText = listOf("© OpenStreetMap"),
                            hyperlinks = listOf("https://www.openstreetmap.org/copyright"),
                            fontSize = 12.sp
                        )
                        HyperlinkText(
                            fullText = "Timezone data tilbydd av TimezoneDB gjennom CC BY 3.0.\n\n\n",
                            linkText = listOf("TimezoneDB"),
                            hyperlinks = listOf("https://timezonedb.com/"),
                            fontSize = 12.sp
                        )
                    }
                }
               }
        }
    }
}


/**
 * This is a custom function that allows the creation of a hyperlink within a block of text.
 * It stores words as a list of strings and hyperlinks as a list of strings.
 *
 * Thanks for the following function provided by StevdzaSan
 * The source code can be found at: https://gist.github.com/stevdza-san/ff9dbec0e072d8090e1e6d16e6b73c91
 */

@Composable
fun HyperlinkText(
    modifier: Modifier = Modifier,
    fullText: String,
    linkText: List<String>,
    linkTextDecoration: TextDecoration = TextDecoration.Underline,
    hyperlinks: List<String> = listOf("https://www.openstreetmap.org/copyright"),
    fontSize: TextUnit = TextUnit.Unspecified

) {
    val contentColor: Color = MaterialTheme.colorScheme.onSurface
    val font: FontFamily = montserratFamily
    val annotatedString = buildAnnotatedString {
        append(fullText)
        linkText.forEachIndexed { index, link ->
            val startIndex = fullText.indexOf(link)
            val endIndex = startIndex + link.length
            addStyle(
                style = SpanStyle(
                    color = contentColor,
                    fontSize = fontSize,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = linkTextDecoration,
                    fontFamily = font
                ),
                start = startIndex,
                end = endIndex
            )
            addStringAnnotation(
                tag = "URL",
                annotation = hyperlinks[index],
                start = startIndex,
                end = endIndex
            )
        }
        addStyle(
            style = SpanStyle(
                fontFamily = font,
                fontWeight = FontWeight.SemiBold,
                fontSize = fontSize,
                color = contentColor
            ),
            start = 0,
            end = fullText.length
        )
    }

    val uriHandler = LocalUriHandler.current

    ClickableText(
        modifier = modifier,
        text = annotatedString,
        onClick = {
            annotatedString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }
        }
    )
}
