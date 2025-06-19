package com.example.nyumbyte.ui.common


import android.annotation.SuppressLint
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asComposeRenderEffect

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nyumbyte.data.model.NavBarItem

@SuppressLint("NewApi")
@Composable
fun CustomNavigationBar(
    items: List<NavBarItem>,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    circleSize: Dp = 40.dp
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    // Calculate initial selected index based on current route
    val initialSelectedIndex = remember {
        items.indexOfFirst { it.route == currentDestination }.takeIf { it >= 0 } ?: 0
    }

    Box (

    ){
        BlurredNavigationBarBackground(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // match your nav bar height
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.1f))
        )
        NavigationBar(
            modifier = modifier
                .graphicsLayer {
                    alpha = 0.9f // slightly see-through
                    shadowElevation = 8f
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    clip = true
                }
                .drawBehind {
                    drawRect(
                        Brush.verticalGradient(
                            colors = listOf(
                                containerColor.copy(alpha = 0.7f),
                                containerColor.copy(alpha = 0.6f)
                            )
                        )
                    )
                }
                .background(Color.Transparent), // light transparent overlay
            tonalElevation = 8.dp,
            containerColor = Color.Transparent, // Transparent to allow gradient
            contentColor = contentColor
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = item.route == currentDestination

                // Animated properties for the circle
                val animatedCircleSize by animateDpAsState(
                    targetValue = if (isSelected) circleSize else 0.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "CircleSizeAnimation"
                )

                // Animated icon size
                val animatedIconSize by animateDpAsState(
                    targetValue = if (isSelected) 28.dp else 24.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "IconSizeAnimation"
                )

                val clickModifier = if (isSelected) Modifier   // no gestures
                else Modifier.pointerInput(Unit) {
                    detectTapGestures { /* same navigate block */ }
                }


                NavigationBarItem(
                    modifier = clickModifier,
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {                       // <- safeguard
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState  = true
                            }
                        }
                    },
                    icon = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(circleSize)
                        ) {
                            // Water-like circle background
                            Box(
                                modifier = Modifier
                                    .size(animatedCircleSize)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                selectedColor.copy(alpha = 0.2f),
                                                selectedColor.copy(alpha = 0.05f)
                                            ),
                                            radius = animatedCircleSize.value * 0.7f
                                        )
                                    )
                            )

                            // Icon on top of the circle
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) selectedColor else unselectedColor,
                                modifier = Modifier.size(animatedIconSize)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.label,
                            color = if (isSelected) selectedColor else unselectedColor,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent, // Hide default indicator
                        selectedIconColor = selectedColor,
                        unselectedIconColor = unselectedColor,
                        selectedTextColor = selectedColor,
                        unselectedTextColor = unselectedColor
                    )
                )
            }
        }
    }
}

@Composable
fun BlurredNavigationBarBackground(
    color: Color,
    modifier: Modifier = Modifier,
    blurRadius: Float = 20f
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Box(
            modifier = modifier
                .graphicsLayer {
                    renderEffect = android.graphics.RenderEffect
                        .createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP)
                        .asComposeRenderEffect()
                }
                .background(color.copy(alpha = 0.2f)) // subtle overlay
        )
    } else {
        Box(modifier = modifier.background(color.copy(alpha = 0.6f)))
    }
}

@Preview(showBackground = true)
@Composable
fun CustomNavigationBarPreview() {
    val dummyItems = listOf(
        NavBarItem("home", Icons.Default.Home, "Home"),
        NavBarItem("search", Icons.Default.Search, "Search"),
        NavBarItem("profile", Icons.Default.Person, "Profile")
    )

    val navController = rememberNavController()

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFECECEC))
        ) {
            CustomNavigationBar(
                items = dummyItems,
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun Modifier.blurBackground(radius: Float = 20f): Modifier = this.graphicsLayer {
    renderEffect = android.graphics.RenderEffect
        .createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
        .asComposeRenderEffect()
}