package com.example.nyumbyte.ui.navigation

import AuthViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.screens.challenges.ChallengeDetailPage
import com.example.nyumbyte.ui.screens.challenges.ChallengePage
import com.example.nyumbyte.ui.screens.dietplanner.DietPlan
import com.example.nyumbyte.ui.screens.dietplanner.DietPlanViewModel
import com.example.nyumbyte.ui.screens.home.Home
import com.example.nyumbyte.ui.screens.login.Login
import com.example.nyumbyte.ui.screens.register.RegisterPhase1
import com.example.nyumbyte.ui.screens.register.RegisterPhase2
import com.example.nyumbyte.ui.screens.register.RegisterSuccessScreen
import com.example.nyumbyte.ui.screens.splash.NBSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.example.nyumbyte.ui.screens.social.SocialPage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun NBNavHost(
    userViewModel: UserViewModel,
    dietPlanViewModel: DietPlanViewModel,
    navController: NavHostController,
    modifier: Modifier,
    authViewModel: AuthViewModel
) {
    // Get current logged-in user's ID (or fallback)
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: "default_user_id"

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

        composable(route = Screens.HomeMain.name) {
            Home(
                navController = navController,
                dietPlanViewModel = dietPlanViewModel
            )
        }

        composable(route = Screens.Login.name) {
            Login(
                viewModel = authViewModel,
                userViewModel = userViewModel,
                navController = navController
            )
        }

        composable(route = Screens.RegisterSuccess.name) {
            RegisterSuccessScreen(
                navController = navController
            )
        }

        composable(route = Screens.DietPlans.name) {
            DietPlan(
                dietPlanViewModel = dietPlanViewModel,
                onGenerateClick = {
                    navController.navigate(Screens.CreateDietPlan.name)
                },
            )
        }

        composable(route = Screens.CreateDietPlan.name) {
            // TODO: Implement CreateDietPlan screen
        }
        composable("challenge_page") {
            ChallengePage(
                onBack = { navController.popBackStack() },
                onChallengeClick = { challengeId ->
                    navController.navigate("challenge_detail/$challengeId")
                },
                onSocialClick = {
                    navController.navigate("social_page")
                }
            )
        }

        composable(
            route = "challenge_detail/{challengeId}",
            arguments = listOf(navArgument("challengeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val challengeId = backStackEntry.arguments?.getString("challengeId")

            val userId = Firebase.auth.currentUser?.uid

            if (userId != null && challengeId != null) {
                ChallengeDetailPage(
                    navController = navController,
                    challengeId = challengeId,
                    userId = userId
                )
            } else {
                // Show loading or error fallback
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Failed to load challenge", color = Color.Red)
                }
            }
        }


        composable("social_page") {
            val userId = Firebase.auth.currentUser?.uid
            if (userId != null) {
                SocialPage(userId = userId)
            } else {
                Text("User not logged in", color = Color.White)
            }
        }





    }
}
