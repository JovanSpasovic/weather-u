package no.uio.ifi.in2000.weatheru.ui.home


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import no.uio.ifi.in2000.weatheru.R
import no.uio.ifi.in2000.weatheru.ui.home.cards.AlertCard
import no.uio.ifi.in2000.weatheru.ui.home.cards.DescriptionCard
import no.uio.ifi.in2000.weatheru.ui.home.cards.LocationTimeCard
import no.uio.ifi.in2000.weatheru.ui.home.cards.MainCard
import no.uio.ifi.in2000.weatheru.ui.home.cards.RainCard
import no.uio.ifi.in2000.weatheru.ui.home.cards.SevenDayCard
import no.uio.ifi.in2000.weatheru.ui.home.cards.temperatureCards.SwipeTomorrowTemperatureCard
import no.uio.ifi.in2000.weatheru.ui.home.cards.temperatureCards.TemperatureCard
import no.uio.ifi.in2000.weatheru.ui.home.cards.temperatureCards.TemperatureGraphCard
import no.uio.ifi.in2000.weatheru.ui.settings.SettingsIcon
import no.uio.ifi.in2000.weatheru.ui.settings.setBackground
import no.uio.ifi.in2000.weatheru.ui.theme.AppTheme
import no.uio.ifi.in2000.weatheru.util.LocationPermissionStatus


@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    navController : NavHostController

) {
   AppTheme {
       val showLoadingAnimation by viewModel.showLoadingAnimation.collectAsState()
       val locationPermissionStatus by viewModel.locationPermissionStatus.collectAsState()


       //  Don't show empty values behind the box asking for permission status
       if (locationPermissionStatus != LocationPermissionStatus.UNDETERMINED) {
            if (showLoadingAnimation) {
                FadingGifImage()
            } else {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(setBackground(viewModel))
                        .padding(bottom = 0.dp)

                ) { // Ensure full width
                    ScrollableCards(viewModel, navController)
                }
            }
        }
    }
}

// Fading animations have not worked and implementations for it are removed

@Composable
fun FadingGifImage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/loading_animation.gif")
                .decoderFactory(GifDecoder.Factory())
                .error(R.drawable.clearsky_night)
                .listener(onError = { _, throwable ->
                    Log.e("Image Loading", "Error loading GIF: $throwable")
                })
                .build(),
            contentDescription = "Loading Gif",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        )
    }
}


@Composable
fun ScrollableCards(viewModel: HomeScreenViewModel, navController: NavHostController) {
    LazyColumn(modifier = Modifier.padding(bottom = 5.dp)) {
        item {
            InternetExceptionSnackbar(viewModel)
        }
        item {
            SettingsIcon(navController)
        }
        item {
            LocationTimeCard(viewModel)
        }
        item {
            AlertCard(viewModel)
        }
        item {
            MainCard(viewModel)
        }
        item {
            DescriptionCard(viewModel)
        }
        item {
            TemperatureCard(viewModel)
        }
        item {
            SwipeTomorrowTemperatureCard(viewModel)
        }
        item {
            RainCard(viewModel)
        }
        item {
            SevenDayCard(viewModel)
        }
        item {
            TemperatureGraphCard(viewModel)
        }

    }
}


