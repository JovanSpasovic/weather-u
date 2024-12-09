package no.uio.ifi.in2000.weatheru.ui.UIState

data class AlertsUIState(
    val eventAwarenessName : String = "",
    val description : String = "",
    val consequences: String = "",
    val instruction: String = "",
    val eventEndingTime: String = "",
    val event: String = "",
    val matrixColor: String = ""
)
