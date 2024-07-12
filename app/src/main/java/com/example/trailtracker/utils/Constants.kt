package com.example.trailtracker.utils

import java.util.Locale
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


object Constants{

    const val START_OR_RESUME_SERVICE = "START_OR_RESUME_SERVICE"
    const val PAUSE_SERVICE = "PAUSE_SERVICE"
    const val STOP_SERVICE = "STOP_SERVICE"

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1

    const val ACTION_SHOW_TRACKING_SCREEN = "ACTION_SHOW_TRACKING_SCREEN"


    fun convertEpochToFormattedDate(epochMillis: Long): String {
        // Define the date formatter with the desired pattern
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy - hh:mm a")
            .withZone(ZoneId.systemDefault())

        // Convert epoch milliseconds to an Instant
        val instant = Instant.ofEpochMilli(epochMillis)

        // Format the Instant to a string
        return formatter.format(instant)
    }


}



fun Long.formatTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val remainingSeconds = this % 60

    return String.format(
        locale = Locale.getDefault(),
        format = "%02d:%02d:%02d",
        hours, minutes, remainingSeconds
    )
}