package com.example.nyumbyte.ui.screens.login

import AuthResult
import AuthViewModel
import PrimaryButton
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.common.AccountTextField
import com.example.nyumbyte.ui.common.RememberCheckBox
import com.example.nyumbyte.ui.navigation.Screens
import com.example.nyumbyte.ui.screens.register.ValidationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-08 12:23:08
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-10 01:38:24
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/login/Login.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */


@Composable
fun Login(
    viewModel: AuthViewModel,
    userViewModel: UserViewModel,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authUiState by viewModel.authUiState.collectAsState()
    
    var checked by remember { mutableStateOf(false) }

    LaunchedEffect(authUiState) {
        if (authUiState.user != null) {
            delay(500)
            authUiState.user?.uid?.let { uid ->
                coroutineScope.launch { userViewModel.loadUser(uid) }
            }

            navController.navigate(Screens.HomeMain.name) {
                popUpTo(Screens.Login.name) { inclusive = true }
            }
        } else if (authUiState.errorMessage != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = authUiState.errorMessage ?: "Login failed. Try again.",
                    withDismissAction = true
                )
            }
            Toast.makeText(context, authUiState.errorMessage ?: "Login failed", Toast.LENGTH_LONG).show()
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
            text = "Welcome Back",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        AccountTextField(
            value = email,
            onValueChange = {
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
                password = it
                passwordError = null
            },
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            error = passwordError
        )
        
        RememberCheckBox(
            checked = checked,
            onChange = {
                checked = it
                viewModel.changeRemember(checked)
                Log.d("Checked", "Final logging $checked, ${authUiState.rememberMe}")
            }
        ) 

        PrimaryButton(
            text = "Login",
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    emailError = if (email.isBlank()) "Email cannot be empty" else null
                    passwordError = if (password.isBlank()) "Password cannot be empty" else null
                    return@PrimaryButton
                }
                viewModel.login(email, password)
            },
            icon = Icons.Default.ArrowForward
        )
    }

    if (authUiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color.White)
                Text(
                    text = "Logging in...",
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}
