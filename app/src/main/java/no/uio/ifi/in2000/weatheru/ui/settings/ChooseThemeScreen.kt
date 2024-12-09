package no.uio.ifi.in2000.weatheru.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.material_theming.montserratFamily
import no.uio.ifi.in2000.weatheru.ui.theme.AppTheme
import no.uio.ifi.in2000.weatheru.ui.theme.MyThemeColors
import no.uio.ifi.in2000.weatheru.util.AutoResizedText


@Composable
fun ChooseThemeScreen(viewModel: HomeScreenViewModel, navController: NavController){

    //When user presses on a specific card, it'll update the themeCode variable.
    // Then I call on the saveTheme function, which saves the themeCode to the sharedPreference document
    //var themeCode by remember { mutableStateOf(viewModel.currentTheme.value) }
    //viewModel.updateTheme(themeCode)

    val themeCardList = listOf(
        Triple("Gradient", " # Standard", MyThemeColors.BackgroundGradient),
        Triple("RainyBliss", " # Regnfull Glede", MyThemeColors.BackgroundRainyBliss),
        Triple("HelloKitty", " # Hello Kitty", MyThemeColors.BackgroundHelloKitty),
        Triple("SunnyMorning", " # Gyllen morgen", MyThemeColors.BackgroundSunnyMorning),
        Triple("Cloudy", " # Skyet", MyThemeColors.BackgroundCloudy),
        Triple("DustyGrass",	" # Lysende gress", MyThemeColors.BackgroundDustyGrass),
        Triple("Summer", " # Sommer", MyThemeColors.BackgroundSummer),
        Triple("Sunset", " # Solnedgang", MyThemeColors.BackgroundSunset),
        Triple("NewYork", " # New York", MyThemeColors.BackgroundNewYork),
        Triple("Rain", " # Regn", MyThemeColors.BackgroundRain),
        Triple("Morpheus", " # Morpheus", MyThemeColors.BackgroundMorpheus),
        Triple("Autumn", " # Høst", MyThemeColors.BackgroundAutumn),
        Triple("Spring", " # Vår", MyThemeColors.BackgroundSpring),
        Triple("Winter", " # Vinter", MyThemeColors.BackgroundWinter),
        Triple("Snow", " # Snø", MyThemeColors.BackgroundSnow)
    )

    AppTheme {
        val contentColor: Color = MaterialTheme.colorScheme.onSurface
        val font: FontFamily = montserratFamily

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(setBackground(viewModel)),
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
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
                Text(
                    text = "Velg bakgrunn",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontFamily = font,
                    fontWeight = FontWeight.Bold
                )
            }



            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .padding(10.dp),
                shape = RoundedCornerShape(30.dp)
            )
            {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = "Informasjon om denne siden",
                        modifier = Modifier.padding(5.dp)
                    )

                    AutoResizedText(
                        text = "Klikk på bakgrunnen du ønsker å bruke.",
                        style = TextStyle.Default,
                        font = 12.sp
                    )
                }
            }

            //Shows different gradient themes
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.Start,
                contentPadding = PaddingValues(20.dp),

                ) {
                items(themeCardList) { item ->
                    ThemeCard(viewModel, item.first, item.second, item.third)
                }
            }
        }
    }
}



@Composable
fun ThemeCard(viewModel: HomeScreenViewModel, themeCode: String, text: String, backgroundColor: Brush) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(8.dp),
            onClick = { viewModel.updateTheme(themeCode) }
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 10.dp, top = 15.dp, end = 10.dp)
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(backgroundColor)
            )
            Text(
                text = text,
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 10.dp, top = 20.dp)
            )
        }
}



