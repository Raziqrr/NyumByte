package com.example.nyumbyte.ui.screens.health

data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val role: String = "user", // required by Gemini API
    val parts: List<GeminiPart>
)


data class GeminiPart(
    val text: String
)
