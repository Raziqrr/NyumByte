/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-10 01:32:12
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-10 01:32:25
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/network/firebase/UserUiState.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.data.network.firebase

import com.example.nyumbyte.data.model.User

sealed class UserUiState {
    object Loading : UserUiState()
    data class Success(val user: User) : UserUiState()
    data class Error(val message: String) : UserUiState()
    object Idle : UserUiState()
}