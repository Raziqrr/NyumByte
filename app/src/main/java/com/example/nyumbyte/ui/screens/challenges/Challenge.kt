package com.example.nyumbyte.ui.screens.challenges

import com.example.nyumbyte.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class Challenge(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "", // "Daily", "Easy", "Hard"
    val imageName: String = "",
    val expReward: Int = 0,
    var completed: Boolean = false

) {
    val imageRes: Int
        get() = when (imageName.lowercase()) {
            "fruit" -> R.drawable.fruitchallenge
            "sugarydrink" -> R.drawable.sugarydrink
            "scan" -> R.drawable.scanchallenge
            "cook" -> R.drawable.cookmeal
            "logmeals" -> R.drawable.logmeals
            else -> R.drawable.sipgod // fallback image
        }
}

// Fallback sample challenges (for preview/offline)
val sampleChallenges = listOf(
    Challenge("1", "Eat a fruit or veggie with each meal", "Daily nutrition task", "Daily", "fruit", 10),
    Challenge("2", "Avoid sugary drinks", "Stay hydrated with water", "Daily", "sugarydrink", 10),
    Challenge("3", "Scan one item with Scan & Swap", "Use app to scan food", "Easy", "scan", 10),
    Challenge("4", "Cook one meal at home", "Prepare and eat at home", "Easy", "cook", 10),
    Challenge("5", "Log meals for a week", "Consistency challenge", "Hard", "logmeals", 25)
)

suspend fun loadChallenges(): List<Challenge> {
    return try {
        val snapshot = Firebase.firestore.collection("challenges").get().await()
        snapshot.documents.mapNotNull { doc ->
            val challenge = doc.toObject(Challenge::class.java)
            val exp = doc.getLong("expReward")?.toInt() ?: challenge?.expReward ?: 0
            challenge?.copy(id = doc.id, expReward = exp)
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun addSampleChallengesToFirestore() {
    val db = Firebase.firestore

    val challenges = listOf(
        Challenge(
            title = "Drink 8 glasses of water",
            description = "Stay hydrated throughout the day.",
            category = "Daily",
            imageName = "fruit",
            expReward = 10,
            completed = false
        ),
        Challenge(
            title = "No junk food for a day",
            description = "Avoid chips, candy, and fast food.",
            category = "Easy",
            imageName = "sugarydrink",
            expReward = 15,
            completed = false
        ),
        Challenge(
            title = "Scan one food item",
            description = "Use the Scan & Swap feature.",
            category = "Easy",
            imageName = "scan",
            expReward = 10,
            completed = false
        ),
        Challenge(
            title = "Cook one meal yourself",
            description = "Make a home-cooked meal.",
            category = "Easy",
            imageName = "cook",
            expReward = 12,
            completed = false
        ),
        Challenge(
            title = "Log your meals for 3 days",
            description = "Track your eating habits consistently.",
            category = "Hard",
            imageName = "logmeals",
            expReward = 25,
            completed = false
        )
    )

    for (challenge in challenges) {
        db.collection("challenges").add(challenge)
            .addOnSuccessListener { docRef ->
                println("✅ Added challenge with ID: ${docRef.id}")
            }
            .addOnFailureListener { e ->
                println("❌ Error adding challenge: $e")
            }
    }
}

