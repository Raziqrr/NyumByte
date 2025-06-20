/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 01:50:49
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-20 10:27:46
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
import com.example.mobileproject.screens.ai_assisstant.AIAssisstantScreen
import com.example.mobileproject.screens.ai_assisstant.ChatViewModel
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
import com.google.accompanist.navigation.animation.composable
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nyumbyte.ui.screens.challenges.Challenge
import com.example.nyumbyte.ui.screens.challenges.ChallengeDetailPage
import com.example.nyumbyte.ui.screens.challenges.ChallengePage
import com.example.nyumbyte.ui.screens.challenges.ChallengeViewModel
import com.example.nyumbyte.ui.screens.health.HealthAnalyticsScreen
import com.example.nyumbyte.ui.screens.profile.ProfileScreen
import com.example.nyumbyte.ui.screens.profile.ProfileViewModel
import com.example.nyumbyte.ui.screens.rewards.RewardViewModel
import com.example.nyumbyte.ui.screens.rewards.RewardsPage
import com.example.nyumbyte.ui.screens.social.SocialPage
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NBNavHost(
    startDestination: String,
    userViewModel: UserViewModel,
    chatViewModel: ChatViewModel,
    dietPlanViewModel: DietPlanViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    rewardViewModel: RewardViewModel? = null,
    profileViewModel: ProfileViewModel,
    uid: String? = null,
    challengeViewModel: ChallengeViewModel
) {
    // Define a reusable animation spec for slide transitions
    val slideAnimationSpec = tween<IntOffset>(
        durationMillis = 400,
        easing = FastOutSlowInEasing
    )

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            when (initialState.destination.route) {
                Screens.SplashScreen.name -> fadeIn(animationSpec = tween(500))
                else -> slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = slideAnimationSpec
                ) + fadeIn(animationSpec = tween(400))
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                Screens.SplashScreen.name -> fadeOut(animationSpec = tween(300))
                else -> slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = slideAnimationSpec
                ) + fadeOut(animationSpec = tween(400))
            }
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -300 },
                animationSpec = slideAnimationSpec
            ) + fadeIn(animationSpec = tween(400))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { 300 },
                animationSpec = slideAnimationSpec
            ) + fadeOut(animationSpec = tween(400))
        }
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

        composable(route = Screens.Home.name) {
            Homepage(
                userViewModel = userViewModel,
                navController = navController,
            )
        }

        composable(route = Screens.HomeMain.name) {
            Home(
                userViewModel,
                dietPlanViewModel,
                authViewModel,
                chatViewModel,
                profileViewModel,
                challengeViewModel
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
                userViewModel = userViewModel,
                dietPlanViewModel = dietPlanViewModel,
                onGenerateClick = {
                    navController.navigate(Screens.CreateDietPlan.name)
                },
                navController = navController
            )
        }

        composable(route = Screens.CreateDietPlan.name) {
            CreateDietPlan(
                navController = navController,
                dietPlanViewModel = dietPlanViewModel,
                userViewModel = userViewModel
            )
        }

        composable(route = Screens.DietPlanResult.name) {
            DietPlanResultScreen(
                dietPlanViewModel = dietPlanViewModel,
                userViewModel = userViewModel,
                navController = navController
            )
        }

        composable(route = Screens.Broco.name) {
            AIAssisstantScreen(navController)
        }

        composable(route = Screens.RewardsPage.name) {
            rewardViewModel?.let {
                RewardsPage(
                    onBack = { navController.popBackStack() },
                    rewardViewModel = it
                )
            }
        }
        
        composable(route = Screens.Profile.name){
            uid?.let {
                ProfileScreen(
                    uid = uid,
                    viewModel = profileViewModel
                )
            }
        }

        composable(
            route = "${Screens.ChallengeDetailBase}/{challengeId}",
            arguments = listOf(navArgument("challengeId") { type = NavType.StringType })
        ) {
            val challengeId = it.arguments?.getString("challengeId")
            uid?.let { safeUid ->
                challengeId?.let { safeChallengeId ->
                    ChallengeDetailPage(
                        navController = navController,
                        challengeId = safeChallengeId,
                        userId = safeUid
                    )
                }
            }
        }
        
        composable(route = Screens.SocialPage.name){
            uid?.let{
                SocialPage(
                    userId = uid,
                    
                    navController = navController
                )
            }
        }


        composable(route = Screens.Health.name){
            uid?.let { HealthAnalyticsScreen(
                uid = uid,
                navController = navController
            )}
        }
        
        
        composable(route = Screens.ChallengePage.name){
            ChallengePage(
                onBack = { navController.popBackStack() },
                onChallengeClick = { challengeId ->
                    navController.navigate(Screens.challengeDetailWithArgs(challengeId))
                },
                onSocialClick = {navController.navigate(Screens.SocialPage.name)},
                viewModel = challengeViewModel
            )
        }

    }
}
