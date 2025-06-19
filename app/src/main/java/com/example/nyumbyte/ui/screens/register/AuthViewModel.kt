/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-09 13:53:37
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-10 01:37:35
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/register/AuthViewModel.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nyumbyte.data.model.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState

    suspend fun getStoredUid(): String? = authRepository.getStoredUid()

    fun storeUid(uid: String) {
        viewModelScope.launch { authRepository.storeUid(uid) }
    }

    fun clearUid() {
        viewModelScope.launch { authRepository.clearUid() }
    }

    fun changeRemember(remember: Boolean){
        Log.d("Checked", "changeRemember called debug 1 $remember, ${_authUiState.value.rememberMe}")
        _authUiState.value = _authUiState.value.copy(rememberMe = remember)
        Log.d("Checked", "changeRemember called debug 2 $remember, ${_authUiState.value.rememberMe}")
    }

    fun register(email: String, password: String) {
        Log.d("AuthViewModel", "register() called with email=$email")

        if (email.isBlank() || password.isBlank()) {
            val msg = "Email and password cannot be empty."
            Log.e("AuthViewModel", msg)
            _authUiState.value = _authUiState.value.copy(errorMessage = msg)
            return
        }

        if (password.length < 6) {
            val msg = "Password must be at least 6 characters long."
            Log.e("AuthViewModel", msg)
            _authUiState.value = _authUiState.value.copy(errorMessage = msg)
            return
        }

        _authUiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            Log.d("AuthViewModel", "Launching coroutine for register()")
            val result = authRepository.createUser(email, password)
            Log.d("AuthViewModel", "register() result: $result")
            handleAuthResult(result)
        }
    }

    fun clearUser() {
        _authUiState.value = AuthUiState(user = null)
    }


    fun login(email: String, password: String) {
        Log.d("AuthViewModel", "login() called with email=$email")

        if (email.isBlank() || password.isBlank()) {
            val msg = "Email and password cannot be empty."
            Log.e("AuthViewModel", msg)
            _authUiState.value = _authUiState.value.copy(errorMessage = msg)
            return
        }
        _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            Log.d("AuthViewModel", "Launching coroutine for login()")
            val result = authRepository.signIn(email, password)
            Log.d("AuthViewModel", "login() result: $result")
            handleAuthResult(result)
        }
    }

    fun logout() {
        Log.d("AuthViewModel", "logout() called")

        // Sign out from Firebase
        authRepository.signOut()

        // Clear stored UID in Room
        viewModelScope.launch {
            authRepository.clearUid()
            Log.d("AuthViewModel", "Stored UID cleared from DAO")
        }

        // Reset UI state
        _authUiState.value = AuthUiState()
        Log.d("AuthViewModel", "User signed out and AuthUiState reset")
    }


    private fun handleAuthResult(result: AuthResult) {
        Log.d("AuthViewModel", "handleAuthResult() called with result=$result")

        when (result) {
            is AuthResult.Success -> {
                val user = result.user
                Log.d("AuthViewModel", "Authentication success, user UID=${user?.uid}, email=${user?.email}")

                // Update state while keeping rememberMe
                _authUiState.value = _authUiState.value.copy(user = user, isLoading = false, errorMessage = null)
                Log.d("AuthViewModel", "Remember me value: ${_authUiState.value.rememberMe}")
                // Now use rememberMe safely
                if (_authUiState.value.rememberMe && user != null) {
                    viewModelScope.launch {
                        authRepository.storeUid(user.uid)
                        Log.d("AuthViewModel", "UID stored in Room: ${user.uid}")
                    }
                }
            }

            is AuthResult.Failure -> {
                val message = result.exception.message ?: "Unknown authentication error"
                Log.e("AuthViewModel", "Authentication failed: $message", result.exception)
                _authUiState.value = _authUiState.value.copy(errorMessage = message, isLoading = false)
            }

            else -> {
                Log.w("AuthViewModel", "handleAuthResult() received unknown result type")
                _authUiState.value = _authUiState.value.copy(errorMessage = "Unknown result from authentication.", isLoading = false)
            }
        }
    }

}
