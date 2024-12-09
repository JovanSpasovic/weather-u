package no.uio.ifi.in2000.weatheru.ui.home.cards

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


class DefaultCardValues {
    @Composable
    fun cardColor(): CardColors {
        return CardDefaults.cardColors(
            containerColor =  MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    @Composable
    fun alertCardColor(matrixColor: String): CardColors {
        return CardDefaults.cardColors(
            containerColor = (getMatrixColorToComposeColor(matrixColor)),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }
    @Composable
    fun cardElevation(): CardElevation {
        return CardDefaults.cardElevation(defaultElevation = 2.dp)
    }

    @Composable
    fun cardCorners(): RoundedCornerShape {
        return RoundedCornerShape(35.dp)
    }


    @Composable
    fun getMatrixColorToComposeColor(matrixColor: String): Color {
        if (matrixColor.lowercase() == "yellow") {
            return Color(0xffffdb58)
        } else if (matrixColor.lowercase() == "orange") {
            return Color(0xffff8b3d)
        } else if (matrixColor.lowercase() == "red") {
            return Color(0xffca3433)
        }
        return MaterialTheme.colorScheme.surface
    }
}
