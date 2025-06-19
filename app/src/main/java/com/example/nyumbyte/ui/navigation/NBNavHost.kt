/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 01:50:49
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-08 23:47:34
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/navigation/NBNavHost.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.navigation
import androidx.compose.runtime.getValue

import AuthViewModel
import android.window.SplashScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nyumbyte.ui.screens.home.Homepage
import com.example.nyumbyte.ui.screens.login.Login
import com.example.nyumbyte.ui.screens.profile.ProfileScreen
import com.example.nyumbyte.ui.screens.profile.ProfileViewModel
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
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = currentRoute == Screens.Home.name || currentRoute == Screens.Profile.name
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Screens.Home to Icons.Default.Home,
                        Screens.Profile to Icons.Default.Person
                    )
                    items.forEach { (screen, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = screen.name) },
                            label = { Text(screen.name) },
                            selected = currentRoute == screen.name,
                            onClick = {
                                if (currentRoute != screen.name) {
                                    navController.navigate(screen.name) {
                                        popUpTo(Screens.Home.name) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        content = { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screens.SplashScreen.name,
                modifier = modifier.then(Modifier.padding(innerPadding))
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

                composable(route = Screens.Home.name) {
                    Homepage()
                }

                composable(route = Screens.Login.name) {
                    Login(
                        authViewModel, navController
                    )
                }

                composable(route = Screens.RegisterSuccess.name) {
                    RegisterSuccessScreen(
                        navController = navController
                    )
                }
                composable(Screens.Profile.name) {


                    val user = authViewModel.authUiState.collectAsState().value.user
                    val uid = user?.uid
                    val profileViewModel: ProfileViewModel = viewModel()

                    if (uid != null) {
                        ProfileScreen( uid = uid,viewModel = profileViewModel)
                    }
                }
            }
        }
    )
        }