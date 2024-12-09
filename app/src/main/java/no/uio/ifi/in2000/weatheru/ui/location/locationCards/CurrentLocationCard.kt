package no.uio.ifi.in2000.weatheru.ui.location.locationCards

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.location.LocationWithName


@OptIn(ExperimentalPagerApi::class)
@Composable
fun CurrentLocationCard(locationWithName: LocationWithName, viewModel: HomeScreenViewModel, pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .alpha(0.8f)
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(vertical = 8.dp)
            .clickable { // Moved clickable from Button to Card
                Log.d("Navigation", "Card clicked")

                viewModel.showCurrentLocationOnHomeScreen()

                coroutineScope.launch {
                    pagerState.animateScrollToPage(1)
                }
            },
        colors = LocationCardDefaultValues().cardColor(),
        shape = LocationCardDefaultValues().cardCorners(),
    ) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Nåværende lokasjon:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Row(verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween){
                Text(
                    text = locationWithName.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 10.dp)
                )
            }
        }
    }
}