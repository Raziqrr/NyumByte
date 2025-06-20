/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-09 14:43:17
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-20 08:20:34
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/home/Home.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.home

import AuthViewModel
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.mobileproject.screens.ai_assisstant.AIAssisstantScreen
import com.example.nyumbyte.data.model.NavBarItem
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.common.CustomNavigationBar
import com.example.nyumbyte.ui.navigation.Screens
import com.example.nyumbyte.ui.screens.challenges.ChallengeDetailPage
import com.example.nyumbyte.ui.screens.challenges.ChallengePage
import com.example.nyumbyte.ui.screens.dietplanner.DietPlan
import com.example.nyumbyte.ui.screens.dietplanner.DietPlanResultScreen
import com.example.nyumbyte.ui.screens.dietplanner.DietPlanViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobileproject.screens.ai_assisstant.ChatViewModel
import com.example.nyumbyte.ui.navigation.NBNavHost
import com.example.nyumbyte.ui.screens.rewards.RewardViewModel

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Home(
    userViewModel: UserViewModel,
    dietPlanViewModel: DietPlanViewModel,
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    rewardViewModel: RewardViewModel
) {
    val homeNavController = rememberNavController()

    val dummyItems = listOf(
        NavBarItem(Screens.Home.name, Icons.Default.Home, "Home"),
        NavBarItem(Screens.Broco.name, Icons.Filled.ChatBubble, "Broco"),
        NavBarItem("Scan", Icons.Filled.CameraAlt, "Scan"),
        NavBarItem("Rewards", Icons.Default.Flag, "Rewards"),
        NavBarItem("profile", Icons.Default.Person, "Profile"),
        NavBarItem(Screens.ChallengePage.name, Icons.Default.Flag, "Challenges")
    )//Dummy list
    val currentDestination = homeNavController.currentBackStackEntryAsState().value?.destination?.route

    val hideBottomBarRoutes = listOf(Screens.Home.name)

    val showBottomBar = currentDestination in hideBottomBarRoutes
    val bottomPadding = if (showBottomBar) 80.dp else 0.dp // Match your nav bar height


    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                CustomNavigationBar(
                    items = dummyItems,
                    navController = homeNavController,
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        
        NBNavHost(
            navController = homeNavController,
            modifier = Modifier.padding(
                bottom = bottomPadding
            ),            
            authViewModel = authViewModel,
            userViewModel = userViewModel,
            dietPlanViewModel = dietPlanViewModel,
            chatViewModel = chatViewModel,
            startDestination = Screens.Home.name,
            rewardViewModel = rewardViewModel
        )
    }
}
