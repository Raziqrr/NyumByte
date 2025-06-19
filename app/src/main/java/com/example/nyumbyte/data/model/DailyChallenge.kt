package com.example.nyumbyte.data.model

import androidx.room.PrimaryKey

data class DailyChallenge(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val expReward: Int = 0,
    val date: String = "", // optional
    val isCompleted: Boolean = false
)
