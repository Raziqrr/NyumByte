package com.example.nyumbyte.ui.screens.challenges

import com.example.nyumbyte.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object ChallengeRepository {

    fun getExpToNextLevel(level: Int): Int {
        return when (level) {
            1 -> 50
            2 -> 100
            3 -> 150
            4 -> 200
            else -> Int.MAX_VALUE // Max level reached
        }
    }

    fun getLevelName(level: Int): String {
        return when (level) {
            1 -> "Avocado Lv.1"
            2 -> "Oats Lv.2"
            3 -> "Salmon Lv.3"
            4 -> "Sweet Potato Lv.4"
            else -> "Berry Lv.${level}"
        }
    }

    fun getLevelIcon(level: Int): Int {
        return when (level) {
            1 -> R.drawable.avocadolvl1
            2 -> R.drawable.oatslvl2
            3 -> R.drawable.salmonlvl3
            4 -> R.drawable.sweetpotatolvl4
            else -> R.drawable.berrylvl5
        }
    }

    suspend fun addExp(userId: String, expToAdd: Int) {
        val db = Firebase.firestore
        val userRef = db.collection("Users").document(userId)

        try {
            val snapshot = userRef.get().await()
            var currentExp = snapshot.getLong("exp")?.toInt() ?: 0
            var currentLevel = snapshot.getLong("level")?.toInt() ?: 1

            currentExp += expToAdd

            // Recalculate level
            while (currentExp >= getExpToNextLevel(currentLevel)) {
                currentExp -= getExpToNextLevel(currentLevel)
                currentLevel++
            }

            userRef.update(
                mapOf(
                    "exp" to currentExp,
                    "level" to currentLevel
                )
            ).await()

        } catch (e: Exception) {
            println("Error updating EXP and level: ${e.localizedMessage}")
        }
    }

    fun getImageResource(imageName: String?): Int {
        return when (imageName?.lowercase()) {
            "fruit" -> R.drawable.fruit
            "veggie" -> R.drawable.veggie
            "water" -> R.drawable.water
            "exercise" -> R.drawable.exercise
            // Add more mappings as needed
            else -> R.drawable.default_image // fallback image
        }
    }
}
