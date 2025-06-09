/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 11:56:31
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-08 17:17:22
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/register/RegisterPhase1.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.register

import AuthViewModel
import PrimaryButton
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nyumbyte.ui.common.AccountTextField
import com.example.nyumbyte.ui.navigation.Screens
import com.example.nyumbyte.ui.theme.NyumByteTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterPhase1(
    viewModel: AuthViewModel,
    navController: NavController,
) {
    val TAG = "RegisterPhase1"

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var loadingMessage by remember { mutableStateOf("Creating account...") }

    val authState by viewModel.authUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    LaunchedEffect(authState) {
        Log.d(TAG, "AuthState changed: $authState")

        when {
            authState.isLoading -> {
                Log.d(TAG, "Authentication in progress...")
            }

            authState.user != null -> {
                Log.d(TAG, "Authentication success. UID=${authState.user!!.uid}")
                loadingMessage = "Finishing up..."
                delay(1000)
                isLoading = false
                navController.navigate(Screens.RegisterDetails.name) {
                    popUpTo(Screens.Register.name) { inclusive = true }
                }
            }

            authState.errorMessage != null -> {
                Log.e(TAG, "Authentication failed: ${authState.errorMessage}")
                isLoading = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = authState.errorMessage ?: "Registration failed. Try again.",
                        withDismissAction = true
                    )
                }
                Toast.makeText(context, authState.errorMessage ?: "Registration failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        AccountTextField(
            value = email,
            onValueChange = {
                Log.d(TAG, "Email input changed: $it")
                email = it
                emailError = null
            },
            label = "Email",
            leadingIcon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            error = emailError
        )

        AccountTextField(
            value = password,
            onValueChange = {
                Log.d(TAG, "Password input changed.")
                password = it
                passwordError = null
            },
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            error = passwordError
        )

        AccountTextField(
            value = confirmPassword,
            onValueChange = {
                Log.d(TAG, "Confirm Password input changed.")
                confirmPassword = it
                confirmError = null
            },
            label = "Confirm Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            error = confirmError
        )

        PrimaryButton(
            text = "Register",
            onClick = {
                Log.d(TAG, "Register button clicked.")
                Log.d(TAG, "Input: email=$email, password=$password, confirm=$confirmPassword")

                val result = validateRegistrationInput(email, password, confirmPassword)

                emailError = result.emailError
                passwordError = result.passwordError
                confirmError = result.confirmError

                if (result.isValid) {
                    Log.d(TAG, "Input validation passed. Starting registration...")
                    isLoading = true
                    loadingMessage = "Creating account..."
                    viewModel.register(email, password)
                } else {
                    Log.d(TAG, "Input validation failed: $result")
                }
            },
            icon = Icons.Default.ArrowForward
        )
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.material3.CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
                androidx.compose.material3.Text(
                    text = loadingMessage,
                    color = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}


fun validateRegistrationInput(
    email: String,
    password: String,
    confirmPassword: String
): ValidationResult {
    var emailErr: String? = null
    var passwordErr: String? = null
    var confirmErr: String? = null

    val TAG = "RegisterValidation"

    Log.d(TAG, "Validating input: email=$email")

    if (!email.contains("@") || !email.contains(".")) {
        emailErr = "Enter a valid email address."
        Log.d(TAG, "Email validation failed: $email")
    }

    if (password.length < 6) {
        passwordErr = "Password must be at least 6 characters."
        Log.d(TAG, "Password too short: $password")
    }

    if (password != confirmPassword) {
        confirmErr = "Passwords do not match."
        Log.d(TAG, "Password mismatch: $password vs $confirmPassword")
    }

    val isValid = emailErr == null && passwordErr == null && confirmErr == null
    Log.d(TAG, "Validation result: isValid=$isValid")

    return ValidationResult(
        isValid = isValid,
        emailError = emailErr,
        passwordError = passwordErr,
        confirmError = confirmErr
    )
}
