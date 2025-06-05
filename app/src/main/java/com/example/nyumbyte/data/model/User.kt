/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 01:12:09
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-06 02:10:31
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/model/User.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.data.model

import androidx.room.PrimaryKey

data class User(
    @PrimaryKey val id: String,
    val userName: String,

    var weight: Double,
    var height: Double,

    var allergies: List<String>,
    val gender: String,
    val ethnicity: String,

    var level: Int,
    var exp: Int,

    val totalPoints: Int,
)