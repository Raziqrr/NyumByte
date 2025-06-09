/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-07 00:01:32
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-10 01:31:47
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/network/firebase/FirestoreRepository.kt
 * @Description: Centralized Firestore functions for user data
 */
package com.example.nyumbyte.data.network.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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

    suspend fun getUserData(uid: String): Map<String, Any>? {
        return try {
            val snapshot = db.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            if (snapshot.exists()) {
                Log.d(TAG, "getUserData: Successfully fetched user data for '$uid'.")
                Log.d(TAG, "getUserData: Successfully fetched user data '${snapshot.data}'.")
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
}
