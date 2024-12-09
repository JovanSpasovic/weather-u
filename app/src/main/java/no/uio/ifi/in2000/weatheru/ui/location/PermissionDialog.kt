package no.uio.ifi.in2000.weatheru.ui.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel


@Composable
fun PermissionDeniedDialog(
    context: Context,
    viewModel: HomeScreenViewModel,
    onDismissRequest: () -> Unit, // This lambda is called to handle dismissal of the dialog
) {
    val textStyle = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Medium
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        // Check if the user changed the permission status
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            viewModel.updateLocationPermissionStatus(true)
            onDismissRequest()
        }
    }


    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Location Permission Denied",
            style = textStyle) },
        text = { Text("Our app needs location permission to provide you with the weather for your current location. Please consider granting the permission.")},
        confirmButton = {
            TextButton(
                onClick = {
                    //PermissionUtils.navigateToAppSettings(context)
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        val uri = Uri.fromParts("package", context.packageName, null)
                        data = uri
                    }
                    requestPermissionLauncher.launch(intent)
                    },
                modifier = Modifier.padding(8.dp)
                ) {
                    Text("App Settings", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Cancel", color = Color.Black)
            }
        }
    )
}