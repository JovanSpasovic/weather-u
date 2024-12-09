package no.uio.ifi.in2000.weatheru.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun SettingsIcon(navController: NavHostController){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, end = 20.dp)
    ) {
        IconButton(onClick = {
            navController.navigate("settings")
        }) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Settings button",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}