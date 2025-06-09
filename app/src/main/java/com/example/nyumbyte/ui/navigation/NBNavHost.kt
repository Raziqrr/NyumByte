/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 01:50:49
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-08 23:47:34
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/navigation/NBNavHost.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.navigation

import AuthViewModel
import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.nyumbyte.ui.screens.home.Homepage
import com.example.nyumbyte.ui.screens.login.Login
import com.example.nyumbyte.ui.screens.register.RegisterPhase1
import com.example.nyumbyte.ui.screens.register.RegisterPhase2
import com.example.nyumbyte.ui.screens.register.RegisterSuccessScreen
import com.example.nyumbyte.ui.screens.splash.NBSplashScreen

@Composable
fun NBNavHost(
    navController: NavHostController,
    modifier: Modifier,
    authViewModel: AuthViewModel // <-- Inject the shared ViewModel here
) {
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.name,
        modifier = modifier
    ) {

        composable(route = Screens.SplashScreen.name) {
            NBSplashScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        
        composable(route = Screens.Register.name) {
            RegisterPhase1(
                navController = navController,
                viewModel = authViewModel,
            )
        }
        composable(route = Screens.RegisterDetails.name) {
            RegisterPhase2(
                authViewModel = authViewModel,
                navController = navController
            )
        }
        
        composable(route = Screens.Home.name){
            Homepage()
        }
        
        composable(route = Screens.Login.name){
            Login(
                authViewModel, navController
            )
        }
        
        composable(route = Screens.RegisterSuccess.name){
            RegisterSuccessScreen(
                navController = navController
            )
        }
    }
}