package com.example.nyumbyte.ui.screens.splash

import AuthViewModel
import PrimaryButton
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.common.SecondaryButton
import com.example.nyumbyte.ui.navigation.Screens
import kotlinx.coroutines.delay

@Composable
fun NBSplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    var isLoading by remember { mutableStateOf(true) }
    var showLoginRegister by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Minimum splash duration (1 second)
        val splashDelay = 1000L
        val startTime = System.currentTimeMillis()

        val storedUid = authViewModel.getStoredUid()
        if (storedUid != null) {
            Log.d("Splash", "Stored UID found: $storedUid. Loading user and navigating to Home.")
            userViewModel.loadUser(storedUid)
            navController.navigate(Screens.HomeMain.name) {
                popUpTo(Screens.SplashScreen.name) { inclusive = true }
            }
        } else {
            Log.d("Splash", "No stored UID found")
        }

        // Calculate remaining time to meet minimum splash duration
        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed < splashDelay) delay(splashDelay - elapsed)

        isLoading = false
        showLoginRegister = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            // Show loading indicator
            CircularProgressIndicator()
        } else if (showLoginRegister) {
            // Show login/register UI
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                PrimaryButton(
                    text = "Register a new account",
                    onClick = { navController.navigate(Screens.Register.name) },
                    icon = null,
                    enabled = true
                )
                SecondaryButton(
                    text = "Login with an existing account",
                    onClick = { navController.navigate(Screens.Login.name) },
                )
            }
        }
    }
}