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