package com.example.trailtracker.utils

import java.util.Locale

object Constants {


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