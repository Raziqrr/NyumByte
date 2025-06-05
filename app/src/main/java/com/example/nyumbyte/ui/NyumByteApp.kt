/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Da  te: 2025-06-06 01:51:30
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-06 01:51:33
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/NyumByteApp.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.nyumbyte.ui.navigation.NBNavHost


@Composable
fun NyumByteApp(
    navController: NavHostController = rememberNavController(),
){
    Scaffold(
        topBar = {
            NBTopAppBar(
                currentScreen = TODO(),
                canNavigateBack = TODO(),
                navigateUp = TODO(),
                modifier = TODO()
            )
        }
    ) {innerPadding->
        NBNavHost(
            navController = navController,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        )
    }
}