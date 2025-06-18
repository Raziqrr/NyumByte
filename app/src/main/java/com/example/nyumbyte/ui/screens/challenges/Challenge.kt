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
        // DAILY (3)
        Challenge(
            title = "Drink 8 glasses of water",
            description = "Stay hydrated throughout the day.",
            category = "Daily",
            imageName = "fruit",
            expReward = 10,
            completed = false
        ),
        Challenge(
            title = "Eat 1 fruit or vegetable",
            description = "Add a fruit or veggie to your meal.",
            category = "Daily",
            imageName = "fruit",
            expReward = 10,
            completed = false
        ),
        Challenge(
            title = "Avoid sugary drinks",
            description = "Only drink water or unsweetened drinks today.",
            category = "Daily",
            imageName = "sugarydrink",
            expReward = 10,
            completed = false
        ),

        // EASY (5)
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
            title = "Take a 15-minute walk",
            description = "Get some light exercise.",
            category = "Easy",
            imageName = "fruit",
            expReward = 10,
            completed = false
        ),
        Challenge(
            title = "Log your breakfast",
            description = "Track your first meal of the day.",
            category = "Easy",
            imageName = "logmeals",
            expReward = 10,
            completed = false
        ),
        Challenge(
            title = "Sleep 7+ hours",
            description = "Ensure a healthy sleep routine.",
            category = "Easy",
            imageName = "sugarydrink",
            expReward = 10,
            completed = false
        ),

        // MEDIUM (5)
        Challenge(
            title = "Log meals for 3 days",
            description = "Track your eating habits consistently.",
            category = "Medium",
            imageName = "logmeals",
            expReward = 20,
            completed = false
        ),
        Challenge(
            title = "Cook 3 meals this week",
            description = "Get creative in the kitchen.",
            category = "Medium",
            imageName = "cook",
            expReward = 20,
            completed = false
        ),
        Challenge(
            title = "Avoid fast food for 3 days",
            description = "Eat homemade or healthy meals.",
            category = "Medium",
            imageName = "sugarydrink",
            expReward = 20,
            completed = false
        ),
        Challenge(
            title = "Scan 5 different foods",
            description = "Use Scan & Swap for variety.",
            category = "Medium",
            imageName = "scan",
            expReward = 20,
            completed = false
        ),
        Challenge(
            title = "Walk 5,000 steps daily for 3 days",
            description = "Be consistently active.",
            category = "Medium",
            imageName = "fruit",
            expReward = 20,
            completed = false
        ),

        // HARD (5)
        Challenge(
            title = "Log meals for a full week",
            description = "Track consistently for 7 days.",
            category = "Hard",
            imageName = "logmeals",
            expReward = 25,
            completed = false
        ),
        Challenge(
            title = "No sugar for 3 days",
            description = "Challenge your cravings.",
            category = "Hard",
            imageName = "sugarydrink",
            expReward = 25,
            completed = false
        ),
        Challenge(
            title = "Cook all meals for 5 days",
            description = "No eating out!",
            category = "Hard",
            imageName = "cook",
            expReward = 30,
            completed = false
        ),
        Challenge(
            title = "Only drink water for a week",
            description = "Cut all other drinks.",
            category = "Hard",
            imageName = "sugarydrink",
            expReward = 30,
            completed = false
        ),
        Challenge(
            title = "Complete all daily challenges for 7 days",
            description = "Stay consistent!",
            category = "Hard",
            imageName = "logmeals",
            expReward = 35,
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


