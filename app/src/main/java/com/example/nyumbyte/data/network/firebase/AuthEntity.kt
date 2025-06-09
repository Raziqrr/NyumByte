/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-08 22:28:32
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-08 22:28:42
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/network/firebase/AuthEntity.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.data.network.firebase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth")
data class AuthEntity(
    @PrimaryKey val id: Int = 0, // Always 0 to keep only 1 UID
    val uid: String
)
