/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-20 06:23:45
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-20 06:23:49
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/aichat/ChatRepository.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.aichat

import com.example.mobileproject.screens.ai_assisstant.ChatMessage

object ChatRepository {
    private var chatHistory: List<ChatMessage> = emptyList()

    fun getChatHistory(): List<ChatMessage> = chatHistory

    fun saveChat(history: List<ChatMessage>) {
        chatHistory = history
    }

    fun clearChat() {
        chatHistory = emptyList()
    }
}