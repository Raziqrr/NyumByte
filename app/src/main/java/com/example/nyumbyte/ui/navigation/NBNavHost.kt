/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 01:50:49
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-06 02:03:22
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/navigation/NBNavHost.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController

@Composable
fun NBNavHost(
    navController: NavHostController,
    modifier: Modifier
){
    NavHost(
        navController = navController,
    ) { }
}