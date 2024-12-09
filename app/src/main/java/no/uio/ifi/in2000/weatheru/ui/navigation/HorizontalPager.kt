package no.uio.ifi.in2000.weatheru.ui.navigation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.location.AddLocationScreen
import no.uio.ifi.in2000.weatheru.ui.theme.AppTheme

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AppNavigation(
    viewModel: HomeScreenViewModel,
    navController: NavHostController
) {
    AppTheme {
        val keyboardController = LocalSoftwareKeyboardController.current
        val pagerState = rememberPagerState(initialPage = 1)
        //  val currentBackStackEntry by navController.currentBackStackEntryAsState()
        //  val isSettingsScreen = currentBackStackEntry?.destination?.route == "settings"

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { currentPage ->
                val route = navController.currentBackStackEntry?.destination?.route
                if (currentPage == 0 && route == "settings") {
                    navController.popBackStack()
                    pagerState.scrollToPage(1)
                }
                if (currentPage == 0 && (route == "chooseTheme" || route == "about")) {
                    navController.popBackStack()
                    navController.popBackStack()
                    pagerState.scrollToPage(1)
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalPager(
                count = 2, // Number of screens
                modifier = Modifier.fillMaxSize(),
                state = pagerState, // Start with HomeScreen
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (page) {
                        0 -> AddLocationScreen(viewModel, pagerState)
                        1 -> NavigationHost(viewModel, navController)
                    }

                }
                LaunchedEffect(pagerState.currentPage) {
                    keyboardController?.hide()
                }
            }


            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-8).dp) // Decrease this value to move the indicator more towards the bottom
                    .padding(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(30.dp) // Makes the Box more rounded
                    )
                    .padding(8.dp)
            ) {

                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = MaterialTheme.colorScheme.onSurface,
                    inactiveColor = MaterialTheme.colorScheme.surface,
                )
            }

        }
    }


}
