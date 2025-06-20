package com.example.nyumbyte.data.network.firebase

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nyumbyte.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


//something
class UserViewModel : ViewModel() {
    private val TAG = "UserViewModel"

    private val _userUiState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val userUiState = _userUiState.asStateFlow()

    private val _debugMessages = MutableStateFlow<List<String>>(emptyList())
    val debugMessages = _debugMessages.asStateFlow()

    fun loadUser(uid: String) {
        viewModelScope.launch {
            Log.d(TAG, "🔄 Loading user data for UID: $uid")
            _userUiState.value = UserUiState.Loading
            _debugMessages.value = listOf("🔄 Loading user data for UID: $uid")

            val data = FirestoreRepository.getUserData(uid)
            if (data != null) {
                try {
                    val user = User.fromMap(data)
                    _userUiState.value = UserUiState.Success(user)
                    Log.d(TAG, "✅ User data loaded successfully.")
                    _debugMessages.value = listOf("✅ User data loaded successfully.")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Parsing error: ${e.localizedMessage}")
                    _userUiState.value = UserUiState.Error("Failed to parse user data.")
                    _debugMessages.value = listOf("❌ Parsing error: ${e.localizedMessage}")
                }
            } else {
                Log.w(TAG, "⚠️ User data could not be retrieved.")
                _userUiState.value = UserUiState.Error("User not found or error occurred.")
                _debugMessages.value = listOf("⚠️ User data could not be retrieved.")
            }
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            Log.d(TAG, "💾 Saving user data for UID: ${user.id}")
            _userUiState.value = UserUiState.Loading
            _debugMessages.value = listOf("💾 Saving user data for UID: ${user.id}")

            val success = FirestoreRepository.saveUserData(user.id, user.toMap())
            if (success) {
                Log.d(TAG, "✅ User data saved successfully.")
                _userUiState.value = UserUiState.Success(user)
                _debugMessages.value = listOf("✅ User data saved successfully.")
            } else {
                Log.e(TAG, "❌ Failed to save user data.")
                _userUiState.value = UserUiState.Error("Failed to save user data.")
                _debugMessages.value = listOf("❌ Failed to save user data.")
            }
        }
    }

    fun updateUserField(uid: String, field: String, value: Any) {
        viewModelScope.launch {
            Log.d(TAG, "✏️ Updating '$field' for user: $uid with value: $value")
            _debugMessages.value = listOf("✏️ Updating '$field' for user: $uid")

            val success = FirestoreRepository.updateUserField(uid, field, value)
            if (success) {
                Log.d(TAG, "✅ Field '$field' updated successfully.")
                _debugMessages.value = listOf("✅ Field '$field' updated successfully.")
                loadUser(uid) // Refresh
            } else {
                Log.e(TAG, "❌ Failed to update field '$field'.")
                _debugMessages.value = listOf("❌ Failed to update field '$field'.")
            }
        }
    }

    fun deleteUser(uid: String) {
        viewModelScope.launch {
            Log.w(TAG, "🗑️ Deleting user with UID: $uid")
            _debugMessages.value = listOf("🗑️ Deleting user with UID: $uid")

            val success = FirestoreRepository.deleteUser(uid)
            if (success) {
                Log.w(TAG, "✅ User '$uid' deleted successfully.")
                _userUiState.value = UserUiState.Idle
                _debugMessages.value = listOf("✅ User '$uid' deleted successfully.")
            } else {
                Log.e(TAG, "❌ Failed to delete user '$uid'.")
                _debugMessages.value = listOf("❌ Failed to delete user '$uid'.")
            }
        }
    }
}
