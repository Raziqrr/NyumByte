package com.example.nyumbyte.util

import java.util.Calendar
import java.util.Locale

fun getCurrentWeekId(): String {
    val calendar = Calendar.getInstance(Locale.getDefault())
    val week = calendar.get(Calendar.WEEK_OF_YEAR)
    val year = calendar.get(Calendar.YEAR)
    return "$year-W$week"
}
