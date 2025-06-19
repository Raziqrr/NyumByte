/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-07 00:01:32
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-08 01:41:05
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/network/firebase/FirestoreRepository.kt
 * @Description: Centralized Firestore functions for user data
 */
package com.example.nyumbyte.data.network.firebase

import android.util.Log
import com.example.nyumbyte.data.model.Challenge
import com.example.nyumbyte.data.model.DailyChallenge
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object FirestoreRepository {

    private const val USERS_COLLECTION = "Users"
    private const val TAG = "FirestoreRepo"
    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun createNewUserIfNotExists(uid: String, data: Map<String, Any>): Boolean {
        return try {
            val docRef = db.collection(USERS_COLLECTION).document(uid)
            val snapshot = docRef.get().await()
            return if (snapshot.exists()) {
                Log.w(TAG, "createNewUserIfNotExists: User with UID '$uid' already exists.")
                false
            } else {
                docRef.set(data).await()
                Log.d(TAG, "createNewUserIfNotExists: New user '$uid' created successfully.")
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "createNewUserIfNotExists: Failed to create user '$uid'.", e)
            false
        }
    }

    suspend fun saveUserData(uid: String, data: Map<String, Any>): Boolean {
        return try {
            db.collection(USERS_COLLECTION)
                .document(uid)
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "saveUserData: User data for '$uid' saved/updated successfully.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "saveUserData: Error saving user data for '$uid'.", e)
            false
        }
    }
    suspend fun initializeDailyChallengeStatusIfMissing(uid: String, date: String, challengeId: String) {
        val db = Firebase.firestore
        val statusDocRef = db.collection("Users")
            .document(uid)
            .collection("daily_challenge_status")
            .document(date)
            .collection("challenges")
            .document(challengeId)

        val snapshot = statusDocRef.get().await()
        if (!snapshot.exists()) {
            statusDocRef.set(mapOf("isCompleted" to false)).await()
            Log.d("Firestore", "Initialized challenge status for $challengeId")
        }
    }
    suspend fun getUserData(uid: String): Map<String, Any>? {
        return try {
            val snapshot = db.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            if (snapshot.exists()) {
                Log.d(TAG, "getUserData: Successfully fetched user data for '$uid'.")
                snapshot.data
            } else {
                Log.w(TAG, "getUserData: No user found for UID '$uid'.")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "getUserData: Error fetching user data for '$uid'.", e)
            null
        }
    }

    suspend fun updateUserField(uid: String, field: String, value: Any): Boolean {
        return try {
            db.collection(USERS_COLLECTION)
                .document(uid)
                .update(field, value)
                .await()
            Log.d(TAG, "updateUserField: Field '$field' updated for user '$uid'.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "updateUserField: Error updating field '$field' for user '$uid'.", e)
            false
        }
    }

    suspend fun deleteUser(uid: String): Boolean {
        return try {
            db.collection(USERS_COLLECTION)
                .document(uid)
                .delete()
                .await()
            Log.d(TAG, "deleteUser: User '$uid' deleted successfully.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "deleteUser: Error deleting user '$uid'.", e)
            false
        }
    }
    suspend fun getDailyChallengesWithStatus(date: String, uid: String): List<DailyChallenge> {
        return try {
            val db = Firebase.firestore

            Log.d("FirestoreRepo", "Fetching challenges for date: $date, uid: $uid")

            val challengesSnapshot = db.collection("daily_challenges")
                .document(date)
                .collection("challenges")
                .get()
                .await()

            Log.d("FirestoreRepo", "Fetched ${challengesSnapshot.size()} challenge docs")

            // 2. Initialize status documents if missing
            for (doc in challengesSnapshot.documents) {
                val challengeId = doc.id
                initializeDailyChallengeStatusIfMissing(uid, date, challengeId)
                Log.d("FirestoreRepo", "Ensured challenge status exists for $challengeId")
            }

            // 3. Fetch user-specific completion status
            val statusSnapshot = db.collection("Users")
                .document(uid)
                .collection("daily_challenge_status")
                .document(date)
                .collection("challenges")
                .get()
                .await()

            Log.d("FirestoreRepo", "Fetched ${statusSnapshot.size()} user status docs")

            val statusMap = statusSnapshot.documents.associateBy { it.id }

            // 4. Combine and return
            challengesSnapshot.mapNotNull { doc ->
                val id = doc.id
                val title = doc.getString("title") ?: ""
                val desc = doc.getString("description") ?: ""
                val reward = (doc.getLong("expReward") ?: 0).toInt()
                val dateField = doc.getString("date") ?: ""
                val completed = statusMap[id]?.getBoolean("isCompleted") == true

                Log.d("FirestoreRepo", "Challenge[$id]: title=$title, completed=$completed")

                DailyChallenge(
                    id = id,
                    title = title,
                    description = desc,
                    expReward = reward,
                    date = dateField,
                    isCompleted = completed
                )
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error fetching daily challenges with status", e)
            emptyList()
        }
    }



    suspend fun markDailyChallengeCompleted(uid: String, date: String, challengeId: String) {
        try {
            Firebase.firestore
                .collection("Users")
                .document(uid)
                .collection("daily_challenge_status")
                .document(date)
                .collection("challenges")
                .document(challengeId)
                .set(mapOf("isCompleted" to true), SetOptions.merge())
                .await()
            Log.d("Firestore", "Marked challenge $challengeId on $date as completed for user $uid.")
        } catch (e: Exception) {
            Log.e("Firestore", "Error marking daily challenge as completed", e)
            throw e
        }
    }

}
