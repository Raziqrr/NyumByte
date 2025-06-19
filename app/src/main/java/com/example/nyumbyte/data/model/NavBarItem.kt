/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-09 14:45:50
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-09 14:46:01
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/model/NavBarItem.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class NavBarItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)
