/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 01:50:49
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-19 17:34:41
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/navigation/NBNavHost.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.navigation

import AuthViewModel
import android.os.Build
import android.window.SplashScreen
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.screens.dietplanner.CreateDietPlan
import com.example.nyumbyte.ui.screens.dietplanner.DietPlan
import com.example.nyumbyte.ui.screens.dietplanner.DietPlanResultScreen
import com.example.nyumbyte.ui.screens.dietplanner.DietPlanViewModel
import com.example.nyumbyte.ui.screens.home.Home
import com.example.nyumbyte.ui.screens.home.Homepage
import com.example.nyumbyte.ui.screens.login.Login
import com.example.nyumbyte.ui.screens.register.RegisterPhase1
import com.example.nyumbyte.ui.screens.register.RegisterPhase2
import com.example.nyumbyte.ui.screens.register.RegisterSuccessScreen
import com.example.nyumbyte.ui.screens.splash.NBSplashScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NBNavHost(
    userViewModel: UserViewModel,
    dietPlanViewModel: DietPlanViewModel,
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
                authViewModel = authViewModel,
                userViewModel = userViewModel
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
        
        composable(route = Screens.HomeMain.name){
            Home(
                userViewModel,
                navController,
                dietPlanViewModel,
            )
        }
        
        composable(route = Screens.Login.name){
            Login(
                viewModel = authViewModel,
                userViewModel = userViewModel,
                navController = navController
            )
        }
        
        composable(route = Screens.RegisterSuccess.name){
            RegisterSuccessScreen(
                navController = navController
            )
        }
        composable(route = Screens.DietPlans.name){
            DietPlan(
                userViewModel = userViewModel,
                dietPlanViewModel = dietPlanViewModel,
                onGenerateClick = {
                    navController.navigate(Screens.CreateDietPlan.name)
                },
                navController = navController
            )
        }
        
        composable(route = Screens.CreateDietPlan.name){
            CreateDietPlan(
                navController = navController,
                dietPlanViewModel = dietPlanViewModel,
                userViewModel = userViewModel
            )
        }
        
        composable(route = Screens.DietPlanResult.name){
            DietPlanResultScreen(
                dietPlanViewModel = dietPlanViewModel,
                userViewModel = userViewModel,
                navController = navController
            )
        }
    }
}