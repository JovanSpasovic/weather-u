package no.uio.ifi.in2000.weatheru.ui.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.material_theming.montserratFamily
import no.uio.ifi.in2000.weatheru.ui.theme.AppTheme


@Composable
fun SettingsScreen(viewModel: HomeScreenViewModel, navController: NavController) {

    Log.d("Setting", "We're in setting Screen")
    val checkedState by viewModel.notificationToggleState.collectAsState()

    LaunchedEffect(checkedState) {
        if (checkedState) {
            viewModel.startRainNotifications()
        } else {
            viewModel.stopRainNotifications()
        }
    }

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
               IconButton(onClick = {
                   navController.popBackStack()
               })
               {
                   Icon(
                       Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                       contentDescription = "Back button",
                       modifier = Modifier.size(40.dp),
                       tint = contentColor
                   ) //Used a Theme.kt color for content.
               }

               Row(
                   modifier = Modifier,
                   horizontalArrangement = Arrangement.Center,
                   verticalAlignment = Alignment.CenterVertically
               ){
                   Text(
                       text = "Innstillinger",
                       fontSize = 24.sp,
                       fontFamily = font,
                       fontWeight = FontWeight.Bold,
                       color = contentColor //Color(0xFF0A2138)
                   )}
           }
           Row(
               modifier = Modifier
                   .padding(all = 16.dp)
                   .fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.CenterVertically
           ) {
               Column(
                   modifier = Modifier
                       .weight(1f) // Adjusted weight
                       .padding(top = 2.dp)
               ) {
                   Text(
                       text = "Daglig værvarsling",
                       fontSize = 14.sp,
                       fontFamily = font,
                       fontWeight = FontWeight.SemiBold,
                       color = contentColor
                   )
                   Text(
                       text = "Beskrivelse av dagens vær sendes som push-notifikasjon til telefonen din",
                       fontSize = 10.sp,
                       fontFamily = font,
                       fontWeight = FontWeight.SemiBold,
                       color = contentColor
                   )
               }
               Switch(
                   checked = checkedState,
                   onCheckedChange = {  state ->
                       viewModel.updateNotificationToggleState(state)
                   },
                   colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.surface,checkedTrackColor = MaterialTheme.colorScheme.onSurface), //Hardcoded needs to be changed
                   modifier = Modifier.padding(all = 8.dp)
               )
           }
           Row(
               modifier = Modifier
                   .clickable(onClick = { navController.navigate("chooseTheme") })
                   .padding(all = 16.dp)
                   .fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.CenterVertically
           ) {
               Text(
                   text = "Velg bakgrunn",
                   fontSize = 14.sp,
                   fontFamily = font,
                   fontWeight = FontWeight.SemiBold,
                   color = contentColor
               )
               IconButton(onClick = {navController.navigate("chooseTheme")}) //Go to customize layout
               {
                   Icon(
                       Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                       contentDescription = "Trykk for å komme til customization-skjermen",
                       modifier = Modifier.size(40.dp),
                       tint = contentColor
                   ) //Used a Theme.kt color for content.
               }

           }
           Row(
               modifier = Modifier
                   .clickable(onClick = { navController.navigate("about") })
                   .padding(all = 16.dp)
                   .fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.CenterVertically
           ) {
               Text(
                   text = "Om appen",
                   fontSize = 14.sp,
                   fontFamily = font,
                   fontWeight = FontWeight.SemiBold,
                   color = contentColor
               )
               IconButton(onClick = {navController.navigate("about")}) //Go to "about app" page
               {
                   Icon(
                       Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                       contentDescription = "Trykk for å lese om appen",
                       modifier = Modifier.size(40.dp),
                       tint = contentColor
                   ) //Used a Theme.kt color for content.
               }
           }
       }
   }
}






