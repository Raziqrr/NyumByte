/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 01:20:33
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-06 02:10:30
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/model/Reward.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.data.model

import androidx.room.PrimaryKey

data class Reward(
    @PrimaryKey val id: String,
    val title: String
)