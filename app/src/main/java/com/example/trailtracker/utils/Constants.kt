package com.example.trailtracker.utils


import java.time.Instant
import java.time.LocalDateTime
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


    object Firebase {
        object User {
            const val USER_REF = "users"
        }
    }

    fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, remainingSeconds)
            else -> String.format("%d:%02d", minutes, remainingSeconds)
        }
    }

    fun formatEpochToDateString(epochMillis: Long, format: String = "MM/dd/yyyy"): String {
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
        return dateTime.format(DateTimeFormatter.ofPattern(format))
    }


}

