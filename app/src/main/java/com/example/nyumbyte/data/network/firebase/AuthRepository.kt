/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-09 13:53:37
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-09 13:53:43
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/network/firebase/AuthRepository.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
import android.util.Log
import com.example.nyumbyte.data.network.firebase.AuthDao
import com.example.nyumbyte.data.network.firebase.AuthEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.lang.Exception

sealed class AuthResult {
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class Failure(val exception: Exception) : AuthResult()
}

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance(), private val authDao: AuthDao) {

    suspend fun createUser(email: String, password: String): AuthResult {
        Log.d("AuthDebug", "createUser called with email=$email")

        if (email.isBlank() || password.isBlank()) {
            val message = "Email and password must not be empty."
            Log.e("AuthError", message)
            return AuthResult.Failure(IllegalArgumentException(message))
        }

        if (password.length < 6) {
            val message = "Password must be at least 6 characters long."
            Log.e("AuthError", message)
            return AuthResult.Failure(IllegalArgumentException(message))
        }

        return try {
            Log.d("AuthDebug", "Attempting to create user...")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            Log.d("AuthDebug", "Firebase createUserWithEmailAndPassword succeeded.")

            if (user != null) {
                Log.d("AuthDebug", "User created successfully with UID: ${user.uid}, email: ${user.email}")

                // Optional: Send email verification
                // user.sendEmailVerification().await()
                // Log.d("AuthDebug", "Verification email sent.")

                AuthResult.Success(user)
            } else {
                val error = "User creation failed: FirebaseUser is null"
                Log.e("AuthError", error)
                AuthResult.Failure(NullPointerException(error))
            }

        } catch (e: Exception) {
            val errorMessage = when (e) {
                is FirebaseAuthWeakPasswordException -> {
                    "Weak password: ${e.reason ?: "The password is too weak."}"
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    "Invalid credentials: ${e.message ?: "Badly formatted email."}"
                }
                is FirebaseAuthUserCollisionException -> {
                    "User already exists: ${e.message ?: "Email is already in use."}"
                }
                else -> {
                    "Unexpected error: ${e.localizedMessage ?: "Unknown error"}"
                }
            }

            Log.e("AuthException", "Exception during user creation", e)
            AuthResult.Failure(Exception(errorMessage, e))
        }
    }


    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(result.user)
        } catch (e: Exception) {
            AuthResult.Failure(e)
        }
    }

    fun signOut() {
        Log.d("AuthRepository", "signOut() called")
        auth.signOut()
        Log.d("AuthRepository", "Firebase signOut() completed")
    }

    fun getCurrentUser(): FirebaseUser? {
        val user = auth.currentUser
        Log.d("AuthRepository", "getCurrentUser() called - user: ${user?.uid ?: "null"}")
        return user
    }

    suspend fun storeUid(uid: String) {
        Log.d("AuthRepository", "storeUid() called with uid=$uid")
        try {
            authDao.insertAuth(AuthEntity(uid = uid))
            Log.d("AuthRepository", "UID stored in Room DB successfully")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to store UID in Room DB", e)
        }
    }

    suspend fun getStoredUid(): String? {
        Log.d("AuthRepository", "getStoredUid() called")
        return try {
            val storedUid = authDao.getAuth().first()?.uid
            Log.d("AuthRepository", "Stored UID fetched: $storedUid")
            storedUid
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to retrieve UID from Room DB", e)
            null
        }
    }

    suspend fun clearUid() {
        Log.d("AuthRepository", "clearUid() called")
        try {
            authDao.clearAuth()
            Log.d("AuthRepository", "UID cleared from Room DB")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to clear UID from Room DB", e)
        }
    }
}
