/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-10 01:32:39
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-10 01:37:34
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/network/firebase/UserViewModel.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.data.network.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nyumbyte.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _userUiState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val userUiState = _userUiState.asStateFlow()

    private val _debugMessages = MutableStateFlow<List<String>>(emptyList())
    val debugMessages = _debugMessages.asStateFlow()

    fun loadUser(uid: String) {
        viewModelScope.launch {
            _userUiState.value = UserUiState.Loading
            _debugMessages.value = listOf("🔄 Loading user data for UID: $uid")

            val data = FirestoreRepository.getUserData(uid)
            if (data != null) {
                try {
                    val user = User.fromMap(data)
                    _userUiState.value = UserUiState.Success(user)
                    _debugMessages.value = listOf("✅ User data loaded successfully.")
                } catch (e: Exception) {
                    _userUiState.value = UserUiState.Error("Failed to parse user data.")
                    _debugMessages.value = listOf("❌ Parsing error: ${e.localizedMessage}")
                }
            } else {
                _userUiState.value = UserUiState.Error("User not found or error occurred.")
                _debugMessages.value = listOf("⚠️ User data could not be retrieved.")
            }
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            _userUiState.value = UserUiState.Loading
            _debugMessages.value = listOf("💾 Saving user data for UID: ${user.id}")

            val success = FirestoreRepository.saveUserData(user.id, user.toMap())
            if (success) {
                _userUiState.value = UserUiState.Success(user)
                _debugMessages.value = listOf("✅ User data saved successfully.")
            } else {
                _userUiState.value = UserUiState.Error("Failed to save user data.")
                _debugMessages.value = listOf("❌ Failed to save user data.")
            }
        }
    }

    fun updateUserField(uid: String, field: String, value: Any) {
        viewModelScope.launch {
            _debugMessages.value = listOf("✏️ Updating '$field' for user: $uid")

            val success = FirestoreRepository.updateUserField(uid, field, value)
            if (success) {
                _debugMessages.value = listOf("✅ Field '$field' updated successfully.")
                loadUser(uid) // Refresh
            } else {
                _debugMessages.value = listOf("❌ Failed to update field '$field'.")
            }
        }
    }

    fun deleteUser(uid: String) {
        viewModelScope.launch {
            _debugMessages.value = listOf("🗑️ Deleting user with UID: $uid")

            val success = FirestoreRepository.deleteUser(uid)
            if (success) {
                _userUiState.value = UserUiState.Idle
                _debugMessages.value = listOf("✅ User '$uid' deleted successfully.")
            } else {
                _debugMessages.value = listOf("❌ Failed to delete user '$uid'.")
            }
        }
    }
}