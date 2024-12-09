package no.uio.ifi.in2000.weatheru.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreen
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.settings.AboutScreen
import no.uio.ifi.in2000.weatheru.ui.settings.ChooseThemeScreen
import no.uio.ifi.in2000.weatheru.ui.settings.SettingsScreen


@Composable
fun NavigationHost(viewModel: HomeScreenViewModel, navController: NavHostController){
    NavHost(navController = navController, startDestination = "homescreen" ){
        composable("homescreen"){
            HomeScreen( viewModel, navController)
        }

        composable("settings"){
            SettingsScreen(viewModel,navController)
        }

        composable("about"){
            AboutScreen(viewModel,navController)
        }
        composable("chooseTheme"){
            ChooseThemeScreen(viewModel,navController)
        }
    }
}