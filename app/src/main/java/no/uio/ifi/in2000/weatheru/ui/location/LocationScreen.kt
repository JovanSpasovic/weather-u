package no.uio.ifi.in2000.weatheru.ui.location

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import no.uio.ifi.in2000.weatheru.R
import no.uio.ifi.in2000.weatheru.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.weatheru.ui.location.locationCards.ClickableLocationCard
import no.uio.ifi.in2000.weatheru.ui.location.locationCards.CurrentLocationCard
import no.uio.ifi.in2000.weatheru.ui.location.locationCards.LocationCardDefaultValues
import no.uio.ifi.in2000.weatheru.ui.material_theming.montserratFamily
import no.uio.ifi.in2000.weatheru.ui.settings.setBackground
import no.uio.ifi.in2000.weatheru.ui.theme.AppTheme
import no.uio.ifi.in2000.weatheru.util.LocationPermissionStatus


@OptIn(ExperimentalPagerApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState", "Range",
    "CoroutineCreationDuringComposition", "StateFlowValueCalledInComposition"
)
@Composable
fun AddLocationScreen(
    viewModel: HomeScreenViewModel,
    pagerState: PagerState
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val searchResults by viewModel.searchResults.collectAsState()
    val locationList by viewModel.locationList.collectAsState()
    val currentLocationWithName by viewModel.currentLocationWithName.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    //val searchJob = remember { mutableStateOf<Job?>(null) }

    // Observe the internet connection status in real-time
    val hasInternet by viewModel.networkStatus.collectAsState()

    //  Update theme in location screen from viewModel
    LaunchedEffect(Unit) {
        viewModel.currentTheme.collect { theme ->
            viewModel.updateTheme(theme)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            keyboardController?.hide()
        }
    }

    if (!hasInternet) {
        NoInternetAccess()
    } else {
        AppTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(setBackground(viewModel))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        colors = OutlinedTextFieldDefaults.colors(
                            cursorColor = MaterialTheme.colorScheme.onSurface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledTextColor= MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = MaterialTheme.colorScheme.surface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                        value = searchText,
                        onValueChange = { newValue ->
                            searchText = newValue
                            // If the searchText is empty, erase the search results
                            if (newValue.isEmpty()) {
                                viewModel.eraseSearchResult()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                // Trigger search when the search button is clicked
                                viewModel.updateSearchResults(searchText)
                            }
                        ),

                        label = {
                            Text(
                                "Søk etter lokasjon",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .alpha(0.8f)
                            .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchText = ""
                                    }
                                ) {
                                    Icon(
                                        painterResource(R.drawable.close),
                                        contentDescription = "Clear text",
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            }
                        }
                    )

                    // Display the search results only when search button is clicked
                    if (searchText.isNotEmpty() && searchResults.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            //  Composable list of search results
                            items(searchResults) { searchResult ->
                                Text(
                                    text = searchResult.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (searchResult.name.isNotBlank()) {
                                                if (locationList.contains(searchResult)) {
                                                    showInfoText(
                                                        context,
                                                        "Denne lokasjonen er allerede lagt til."
                                                    )
                                                } else {
                                                    viewModel.addLocationToLocationList(searchResult)
                                                }
                                            } else {
                                                showInfoText(
                                                    context,
                                                    "Informasjon for dette landet ble ikke funnet."
                                                )
                                            }
                                            viewModel.eraseSearchResult()
                                            searchText = ""
                                            focusManager.clearFocus()
                                            keyboardController?.hide()
                                        }
                                        .padding(16.dp)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = 0.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Mine lokasjoner\n",
                            fontSize = 24.sp,
                            fontFamily = montserratFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        // If location permission is granted, show the current location card
                        if (viewModel.getCurrentLocationPermissionStatus() == LocationPermissionStatus.GRANTED) {
                            if (currentLocationWithName != LocationWithName()) { // Check that it is not default valued
                                CurrentLocationCard(
                                    currentLocationWithName,
                                    viewModel,
                                    pagerState
                                )
                            }
                        } else {
                            Card(
                                modifier = Modifier
                                    .alpha(0.8f)
                                    .fillMaxWidth()
                                    .height(intrinsicSize = IntrinsicSize.Max)
                                    .padding(vertical = 8.dp)
                                    .clickable { showDialog = true },
                                colors = LocationCardDefaultValues().cardColor(),
                                shape = LocationCardDefaultValues().cardCorners(),

                                ) {
                                Row(
                                    modifier = Modifier
                                        .padding(all = 16.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Trykk for å aktivere din nåværende lokasjon",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        // If location permission is not granted, show a card with a message to activate current location
                        if (showDialog) {
                            PermissionDeniedDialog(
                                context = context,
                                viewModel = viewModel,
                                onDismissRequest = { showDialog = false }
                            )
                        }
                    }
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(locationList) { location ->
                            ClickableLocationCard(
                                locationWithName = location,
                                viewModel,
                                pagerState
                            )
                            }
                        }
                    }
                }
            }
        }
    }


fun showInfoText(context: Context, text: String) {
    Toast.makeText(
        context,
        text,
        Toast.LENGTH_SHORT
    ).show()
}
