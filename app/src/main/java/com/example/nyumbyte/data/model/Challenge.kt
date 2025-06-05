/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 01:19:35
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-06 01:23:35
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/model/Challenge.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.data.model

data class Challenge(
    val id: Int,
    val title: String,
    val description: String,
    val expReward: Int,
    val imagePath: String,
    val category: String
)