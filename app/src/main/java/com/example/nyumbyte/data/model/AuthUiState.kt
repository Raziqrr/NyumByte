/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-08 17:13:35
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-08 22:46:22
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/model/AuthUiState.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.data.model

import com.google.firebase.auth.FirebaseUser

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val errorMessage: String? = null,
    val rememberMe: Boolean = false
)
