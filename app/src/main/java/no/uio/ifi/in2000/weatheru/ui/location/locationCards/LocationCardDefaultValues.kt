package no.uio.ifi.in2000.weatheru.ui.location.locationCards


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

class LocationCardDefaultValues {
    @Composable
    fun cardColor(): CardColors {
        return CardDefaults.cardColors(
            containerColor =  MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    @Composable
    fun cardElevation(): CardElevation {
        return CardDefaults.cardElevation(defaultElevation = 8.dp)
    }

    @Composable
    fun cardCorners(): RoundedCornerShape {
        return RoundedCornerShape(8.dp)
    }
}
