/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-08 22:55:23
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-10 01:41:20
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/splash/NBSplashScreen.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.splash

import AuthViewModel
import PrimaryButton
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.common.SecondaryButton
import com.example.nyumbyte.ui.navigation.Screens
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun NBSplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
){

    LaunchedEffect(Unit) {
        val storedUid = authViewModel.getStoredUid()
        if (storedUid != null) {
            Log.d("Splash", "Stored UID found: $storedUid. Navigating to Home.")
            userViewModel.loadUser(storedUid)
            navController.navigate(Screens.HomeMain.name) {
                popUpTo(Screens.SplashScreen.name) { inclusive = true }
            }
        }
    }
    Column(
        modifier = Modifier.padding(10.dp)
    ) { 
        PrimaryButton(
            text = "Register a new account",
            onClick = {
                navController.navigate(Screens.Register.name)
            },
            icon = null,
            enabled = true
        )
        SecondaryButton(
            text = "Login with an existing account",
            onClick = {
                navController.navigate(Screens.Login.name)
            },
        )
    }
}