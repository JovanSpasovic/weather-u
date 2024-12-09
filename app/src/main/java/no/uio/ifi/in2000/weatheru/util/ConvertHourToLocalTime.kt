package no.uio.ifi.in2000.weatheru.util



fun convertDateToLocalTimeInHours(dateString: String, gmtOffset: Int): String {
    val hour = dateString.substringAfter("T").substringBefore(":").toInt()
    val totalHours = gmtOffset / 3600 + hour
    val localHour = totalHours % 24  // Adjust hour to be within 0-23 range for safety
    if (localHour < 0) {                    //  If timeZone offset is negative, subtract the offset from 24
        return (24+localHour).toString()    //  In practise, the localHour variable is added as it already is negative
    } else if (localHour < 10) {
        return "0$localHour"
    }
    return localHour.toString()
}

