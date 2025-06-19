/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-09 14:43:17
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-19 17:34:42
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/home/Home.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.home
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.nyumbyte.data.model.NavBarItem
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.common.CustomNavigationBar
import com.example.nyumbyte.ui.navigation.Screens
import com.example.nyumbyte.ui.screens.challenges.ChallengeDetailPage
import com.example.nyumbyte.ui.screens.challenges.ChallengePage
import com.example.nyumbyte.ui.screens.dietplanner.DietPlan
import com.example.nyumbyte.ui.screens.dietplanner.DietPlanResultScreen
import com.example.nyumbyte.ui.screens.dietplanner.DietPlanViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(
    userViewModel: UserViewModel,
    navController: NavHostController,
    dietPlanViewModel: DietPlanViewModel
) {
    val homeNavController = rememberNavController()

    val dummyItems = listOf(
        NavBarItem(Screens.Home.name, Icons.Default.Home, "Home"),
        NavBarItem("AI Chat", Icons.Filled.ChatBubble, "Broco"),
        NavBarItem("Scan", Icons.Filled.CameraAlt, "Scan"),
        NavBarItem("Rewards", Icons.Default.Flag, "Rewards"),
        NavBarItem("profile", Icons.Default.Person, "Profile"),
        NavBarItem("challenge_page", Icons.Default.Flag, "Challenges")
    )//Dummy list

    Scaffold(
        bottomBar = {
            CustomNavigationBar(
                items = dummyItems,
                navController = navController,
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = homeNavController,
            startDestination = Screens.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screens.Home.name) {
                Homepage(
                    userViewModel =userViewModel,
                    navController = navController
                )
            }
            composable("profile_tab") {
                Text("Profile Content")
            }
            composable("settings_tab") {
                Text("Settings Content")
            }
            composable(route = Screens.DietPlans.name){
                DietPlan(
                    userViewModel = userViewModel,
                    dietPlanViewModel = dietPlanViewModel,
                    onGenerateClick = {
                        
                    },
                    navController = navController
                )
            }

            composable(route = Screens.DietPlanResult.name){
                DietPlanResultScreen(
                    dietPlanViewModel = dietPlanViewModel,
                    userViewModel = userViewModel,
                    navController = navController
                )
            }
            composable(
                route = "challenge_detail/{challengeId}/{userId}",
                arguments = listOf(
                    navArgument("challengeId") { type = NavType.StringType },
                    navArgument("userId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val challengeId = backStackEntry.arguments?.getString("challengeId")
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable

                ChallengeDetailPage(
                    navController = navController,
                    challengeId = challengeId,
                    userId = userId
                )
            }



        }
    }
}
