package com.example.nyumbyte.data.repository

import android.util.Log
import com.example.nyumbyte.data.model.Health
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object HealthRepository {

    private const val USERS_COLLECTION = "Users"
    private const val HEALTH_SUBCOLLECTION = "health_data"
    private val db = FirebaseFirestore.getInstance()

    suspend fun saveHealthData(uid: String, week: String, health: Health): Boolean {
        return try {
            db.collection(USERS_COLLECTION)
                .document(uid)
                .collection(HEALTH_SUBCOLLECTION)
                .document(week)
                .set(health.toMap(), SetOptions.merge())
                .await()
            Log.d("HealthRepository", "Health data saved for $uid - week $week")
            true
        } catch (e: Exception) {
            Log.e("HealthRepository", "Failed to save health data", e)
            false
        }
    }
    suspend fun getUserHealthInfo(uid: String): Triple<Double, Double, List<String>>? {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .get()
                .await()

            if (snapshot.exists()) {
                val weight = snapshot.getDouble("weight") ?: 0.0
                val height = snapshot.getDouble("height") ?: 0.0
                val allergies = snapshot.get("allergies") as? List<String> ?: emptyList()
                Triple(weight, height, allergies)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getHealthData(uid: String, week: String): Health? {
        return try {
            val snapshot = db.collection("Users")
                .document(uid)
                .collection("health_data")
                .document(week)
                .get()
                .await()

            if (snapshot.exists()) {
                val data = snapshot.data ?: return null

                return Health.fromMap(data)

            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("HealthRepo", "Error fetching health data", e)
            null
        }
    }

    suspend fun saveUserBMI(uid: String, bmi: Double): Boolean {
        return try {
            db.collection(USERS_COLLECTION)
                .document(uid)
                .set(mapOf("bmi" to bmi), SetOptions.merge())
                .await()
            Log.d("HealthRepository", "BMI saved for user $uid")
            true
        } catch (e: Exception) {
            Log.e("HealthRepository", "Failed to save BMI", e)
            false
        }
    }

}
