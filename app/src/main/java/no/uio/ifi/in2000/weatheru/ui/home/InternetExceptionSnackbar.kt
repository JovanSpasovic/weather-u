package no.uio.ifi.in2000.weatheru.ui.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun InternetExceptionSnackbar(viewModel: HomeScreenViewModel) {

    // Observe the networkStatus state in the Composable function
    val hideSnackbar by viewModel.networkStatus.collectAsState()

    if (!hideSnackbar) {
        Snackbar(
            action = {
                Button(
                    colors = ButtonColors(
                        containerColor =  MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface),
                    onClick = {
                    viewModel.retryForecastData()
                    // Do not hide the snackbar when the button is clicked
                }) {
                    Text("Retry")
                }
            },
            modifier = Modifier
                .padding(2.dp)
                .background(Color.Transparent)
        ) {
            Text(text = "No internet connection!")
        }
    }
}