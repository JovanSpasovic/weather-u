package no.uio.ifi.in2000.weatheru.ui.settings

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Brush
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.theme.MyThemeColors


@Composable
fun setBackground(viewModel: HomeScreenViewModel): Brush {
    val themeCode = viewModel.currentTheme.collectAsState().value //gets the themecode from the sharedpreference file

    //The log returns correct, until I go to chooseBackground it then returns an empty string.
    //Meaning it is probably a logical error
    Log.d("Theme", "themecode is $themeCode")

    if (isSystemInDarkTheme()) {
        return MyThemeColors.BackgroundGradientDark

    } else {
        return when (themeCode) {
            "Sunset" -> {
                MyThemeColors.BackgroundSunset
            }

            "NewYork" -> {
                MyThemeColors.BackgroundNewYork
            }

            "Rain" -> {
                MyThemeColors.BackgroundRain
            }

            "HelloKitty" -> {
                MyThemeColors.BackgroundHelloKitty
            }

            "Morpheus" -> {
                MyThemeColors.BackgroundMorpheus
            }

            "Autumn" -> {
                MyThemeColors.BackgroundAutumn
            }

            "Spring" -> {
                MyThemeColors.BackgroundSpring
            }

            "Summer" -> {
                MyThemeColors.BackgroundSummer
            }

            "Winter" -> {
                MyThemeColors.BackgroundWinter
            }

            "Snow" -> {
                MyThemeColors.BackgroundSnow
            }
            "RainyBliss" -> {
                MyThemeColors.BackgroundRainyBliss
            }
            "SunnyMorning" -> {
                MyThemeColors.BackgroundSunnyMorning
            }
            "Cloudy" -> {
                MyThemeColors.BackgroundCloudy
            }
            "DustyGrass" -> {
                MyThemeColors.BackgroundDustyGrass
            }

            else -> {
                MyThemeColors.BackgroundGradient
            }
        }
    }
}
