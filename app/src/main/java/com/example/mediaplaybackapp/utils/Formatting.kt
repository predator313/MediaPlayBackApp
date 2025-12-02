package com.example.mediaplaybackapp.utils

import androidx.compose.ui.text.intl.Locale

fun formatMsToString(timeInMs: Long): String {
    var seconds = timeInMs/1000
    val hours = seconds/3600
    seconds -= hours*3600
    val minutes = seconds/60
    seconds -= minutes*60
    return String.format(java.util.Locale.getDefault(), "%02d:%02d:%02d", hours,minutes,seconds)
}