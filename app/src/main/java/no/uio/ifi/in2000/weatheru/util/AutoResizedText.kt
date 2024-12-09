package no.uio.ifi.in2000.weatheru.util


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit


@Composable
fun AutoResizedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    font: TextUnit
) {
    var resizedTextStyle by remember {
        mutableStateOf(style)
    }

    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        fontSize = font,
        style = resizedTextStyle, // Use resizedTextStyle here
        modifier = modifier.drawWithContent {
            drawContent()
        },
        softWrap = false,
        onTextLayout = { result ->
            // Check if text overflowed its width
            if (result.didOverflowWidth) {
                // Reduce font size by 5% if overflow occurred
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = resizedTextStyle.fontSize * 0.95,
                )
            }
        }
    )
}