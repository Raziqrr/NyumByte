package com.example.nyumbyte.ui.screens.social

import androidx.compose.runtime.Composable
import com.example.nyumbyte.ui.screens.social.components.SocialFeed

@Composable
fun SocialPage(userId: String) {
    SocialFeed(userId = userId)
}
