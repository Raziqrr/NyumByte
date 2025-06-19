/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 01:19:35
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-06 02:10:31
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/model/Challenge.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.data.model


import com.example.nyumbyte.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

// Main Challenge Data Class
data class Challenge(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val expReward: Int = 0,
    val imageName: String = "", // Used to resolve image from name
    val completedBy: Map<String, Boolean> = emptyMap(), // Stored in Firestore

    // App-computed fields
    val docId: String = "",
    val completed: Boolean = false
) {
    // Maps the image name to the corresponding drawable resource
    val challengeImageRes: Int
        get() = when (imageName.lowercase()) {
            "run" -> R.drawable.run
            "fruit" -> R.drawable.fruit
            "water" -> R.drawable.water
            "veggie" -> R.drawable.veggie
            "sugarydrink" -> R.drawable.sugarydrink
            "scan" -> R.drawable.ic_challenge_scan
            "cook" -> R.drawable.cook
            "logmeals" -> R.drawable.logmeals
            else -> R.drawable.default_challenge // fallback image
        }
}






suspend fun loadChallengesForUser(userId: String): List<Challenge> {
    return try {
        val snapshot = Firebase.firestore.collection("challenges").get().await()
        snapshot.documents.mapNotNull { doc ->
            val challenge = doc.toObject(Challenge::class.java)
            val completedMap = doc.get("completedBy") as? Map<String, Boolean> ?: emptyMap()
            challenge?.copy(
                docId = doc.id,
                completed = completedMap[userId] == true
            )
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
            imageName = "water",
            expReward = 10,
            completedBy = emptyMap()
        ),
        Challenge(
            title = "Eat 1 fruit or vegetable",
            description = "Add a fruit or veggie to your meal.",
            category = "Daily",
            imageName = "fruit",
            expReward = 10,
            completedBy = emptyMap()
        ),
        Challenge(
            title = "Avoid sugary drinks",
            description = "Only drink water or unsweetened drinks today.",
            category = "Daily",
            imageName = "sugarydrink",
            expReward = 10,
            completedBy = emptyMap()
        ),
        Challenge(
            title = "Scan one food item",
            description = "Use the Scan & Swap feature.",
            category = "Easy",
            imageName = "scan",
            expReward = 10,
            completedBy = emptyMap()
        ),
        Challenge(
            title = "Cook one meal yourself",
            description = "Make a home-cooked meal.",
            category = "Easy",
            imageName = "cook",
            expReward = 12,
            completedBy = emptyMap()
        ),
        Challenge(
            title = "Log meals for a full week",
            description = "Track consistently for 7 days.",
            category = "Hard",
            imageName = "logmeals",
            expReward = 25,
            completedBy = emptyMap()
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
