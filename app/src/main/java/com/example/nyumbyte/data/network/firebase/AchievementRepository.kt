package com.example.nyumbyte.data.repository

import android.util.Log
import com.example.nyumbyte.data.model.Achievement
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ktx.firestore


object AchievementRepository {

    private val db = FirebaseFirestore.getInstance()
    private const val TAG = "AchievementRepo"

    suspend fun saveAchievement(uid: String, achievementId: String, data: Map<String, Any>): Boolean {
        return try {
            db.collection("Users")
                .document(uid)
                .collection("achievements")
                .document(achievementId)
                .set(data)
                .await()
            Log.d(TAG, "Achievement '$achievementId' saved successfully for user $uid.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving achievement '$achievementId' for user $uid.", e)
            false
        }
    }

    suspend fun getAchievements(uid: String): List<Achievement> {
        return try {
            val snapshot = Firebase.firestore
                .collection("Users")
                .document(uid)
                .collection("achievements")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val title = doc.getString("title") ?: return@mapNotNull null
                val desc = doc.getString("description") ?: ""
                val date = doc.getString("date") ?: ""
                val level = doc.getLong("level")?.toInt()
                val type = doc.getString("type") ?: ""
                Achievement(title = title, description = desc, date = date, level = level, type = type)

            }
        } catch (e: Exception) {
            Log.e("AchievementRepo", "Error fetching achievements", e)
            emptyList()
        }
    }

}
