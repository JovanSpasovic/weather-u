package no.uio.ifi.in2000.weatheru.ui.location.locationCards

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.location.LocationWithName

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ClickableLocationCard(
    locationWithName: LocationWithName,
    viewModel: HomeScreenViewModel,
    pagerState: PagerState
) {

    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .alpha(0.8f)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp)
            .clickable(onClick = {
                Log.d("Navigation", "Card clicked")
                Log.d("Navigation", "TEST LOCATION NAME NAVIGATION location name: ${locationWithName.name}")
                Log.d("Navigation", "TEST LOCATION NAME NAVIGATION location coordinates: ${locationWithName.location}")

                locationWithName.location?.let { location ->
                    viewModel.updateOtherLocationFromLocationScreen(location)
                }
                coroutineScope.launch {
                    pagerState.animateScrollToPage(1)
                }
            }),
        colors = LocationCardDefaultValues().cardColor(),
        shape = LocationCardDefaultValues().cardCorners(),
    ){

        Row(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = formattedLocationName(locationWithName),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 10.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = {
                    viewModel.removeLocationFromLocationList(locationWithName)
                },
                modifier = Modifier,
            ) {
                Icon(Icons.Default.Clear, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun formattedLocationName(locationWithName: LocationWithName) : String {
    val first = locationWithName.name.substringBefore(", ")
    val third = locationWithName.name.substringAfterLast(",  ")
    return "$first, $third"
}