/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Da  te: 2025-06-06 01:51:30
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-06 01:51:33
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/NyumByteApp.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui

import AuthRepository
import AuthViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mobileproject.screens.ai_assisstant.ChatViewModel
import com.example.nyumbyte.data.network.firebase.AuthDatabase
import com.example.nyumbyte.data.network.firebase.FirestoreRepository
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.navigation.NBNavHost
import com.example.nyumbyte.ui.navigation.Screens
import com.example.nyumbyte.ui.screens.challenges.ChallengeViewModel
import com.example.nyumbyte.ui.screens.dietplanner.DietPlanViewModel
import com.example.nyumbyte.ui.screens.foodscanner.FoodScannerViewModel
import com.example.nyumbyte.ui.screens.profile.ProfileViewModel
import com.example.nyumbyte.ui.screens.rewards.RewardViewModel


@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun NyumByteApp(
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp)
    ) {innerPadding ->
        val context = LocalContext.current.applicationContext

        val authDao = remember { AuthDatabase.getInstance(context).authDao() }

        val authViewModel: AuthViewModel = viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val authRepository = AuthRepository(authDao = authDao)
                    return AuthViewModel(authRepository) as T
                }
            }
        )

        val userViewModel: UserViewModel = viewModel()
        val dietPlanViewModel: DietPlanViewModel = viewModel()
        val chatViewModel: ChatViewModel = viewModel()
        val profileViewModel: ProfileViewModel = viewModel()
        val challengeViewModel: ChallengeViewModel = viewModel()
        val foodScannerViewModel: FoodScannerViewModel = viewModel()


        NBNavHost(
            navController = navController,
            authViewModel = authViewModel,
            userViewModel = userViewModel,
            dietPlanViewModel = dietPlanViewModel,
            chatViewModel = chatViewModel,
            startDestination = Screens.SplashScreen.name,
            rewardViewModel = null,
            profileViewModel = profileViewModel,
            challengeViewModel = challengeViewModel,
            foodScannerViewModel = foodScannerViewModel
        )
    }
}
