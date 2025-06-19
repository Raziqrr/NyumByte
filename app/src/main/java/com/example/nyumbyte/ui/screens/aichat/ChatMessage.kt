/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-05-08 17:28:35
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-05-15 22:50:50
 * @FilePath: app/src/main/java/com/example/mobileproject/screens/ai_assisstant/ChatMessage.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.mobileproject.screens.ai_assisstant

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val message: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()    
)
