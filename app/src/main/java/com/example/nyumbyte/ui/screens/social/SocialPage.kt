package com.example.nyumbyte.ui.screens.social

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.nyumbyte.ui.screens.social.components.SocialFeed

@Composable
fun SocialPage(userId: String, navController: NavController) {
    SocialFeed(userId = userId, navController = navController)
}
