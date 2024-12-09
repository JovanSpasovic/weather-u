package no.uio.ifi.in2000.weatheru.ui.UIState

import no.uio.ifi.in2000.weatheru.model.nominatim.Address

data class ReverseGeocodingUIState (
    val displayName : String?,

    val addressAndCountry : Pair<Address, String>?
)