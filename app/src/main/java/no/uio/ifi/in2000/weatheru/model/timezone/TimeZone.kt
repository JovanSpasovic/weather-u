
data class TimeZoneData(
    val zoneName: String = "",
    val formatted: String = "",  // Direct formatted time from the API
    val gmtOffset: Int =  0      // Offset in seconds from UTC
)
