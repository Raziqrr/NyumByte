/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-09 14:47:31
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-09 14:56:37
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/common/CustomNavigationBar.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.common
import androidx.compose.material3.NavigationBarItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nyumbyte.data.model.NavBarItem

@Composable
fun CustomNavigationBar(
    items: List<NavBarItem>,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(containerColor)
            .shadow(4.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        tonalElevation = 8.dp,
        containerColor = containerColor,
        contentColor = contentColor
    ) {
        items.forEach { item ->
            val selected = currentDestination == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) selectedColor else unselectedColor,
                        modifier = Modifier.size(if (selected) 28.dp else 24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selected) selectedColor else unselectedColor,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = selectedColor.copy(alpha = 0.1f),
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unselectedColor
                )
            )
        }
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

    CustomNavigationBar(
        items = dummyItems,
        navController = navController
    )
}