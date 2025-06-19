/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-20 07:46:10
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-20 07:46:10
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/network/gemini/GeminiProvider.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.mobileproject.ai

import com.example.nyumbyte.data.network.gemini.brocoPersonality
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.content

object GeminiProvider {

    val model by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = "AIzaSyAjki-horKZfIdi9TQ0FSPExmhXNtIwHVk",
            systemInstruction = content(role = "system") {
                text(brocoPersonality)
            },
            requestOptions = RequestOptions()
        )
    }
}