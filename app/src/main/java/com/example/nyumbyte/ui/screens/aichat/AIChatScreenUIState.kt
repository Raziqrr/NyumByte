/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-05-08 17:30:25
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-05-08 19:11:42
 * @FilePath: app/src/main/java/com/example/mobileproject/screens/ai_assisstant/AIChatScreenUIState.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.mobileproject.screens.ai_assisstant

data class AIChatScreenUIState(
    val chatHistory: List<ChatMessage> = emptyList(),
    val currentInput: String = "",
    val isLoading: Boolean = false,
    val botName: String = "AI Assistant",
    val botAvatarUrl: String? = null, // optional, for personality,
    val isTyping: Boolean = false // <- new field
)