package com.example.nyumbyte.ui.screens.foodscanner

import com.example.mobileproject.ai.GeminiProvider

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nyumbyte.ui.screens.aichat.ChatRepository
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GeminiResult(
    val calorie: String,
    val suggestion: String
)

suspend fun fetchGeminiSuggestion(label: String): GeminiResult = withContext(Dispatchers.IO) {
    val prompt = """
        For the food "$label":
        1. Estimate its calorie content in kilocalories (kcal). Be specific and give a single number only (no ranges, no words like 'about', no explanation).
        2. Suggest 2 healthier alternatives in the same food category, with a short but informative reason why each is healthier.

        Format your answer like:
        Calories: <number> kcal
        Suggestions:
        1. <food name> – <short description>
        2. <food name> – <short description>
    """.trimIndent()

    return@withContext try {
        val response = GeminiProvider.model.generateContent(prompt)
        val text = response.text ?: ""

        // Extract exact calorie number
        val calorieLine = text.lines().find { it.contains("Calories:", ignoreCase = true) } ?: "Calories: 0"
        val calorie = Regex("""\d+""").find(calorieLine)?.value ?: "0"

        // Extract suggestion text
        val suggestionStart = text.indexOf("Suggestions:")
        val suggestion = if (suggestionStart != -1) {
            text.substring(suggestionStart).replace("Suggestions:", "").trim()
        } else {
            "No suggestions found."
        }

        GeminiResult(calorie = calorie, suggestion = suggestion)

    } catch (e: Exception) {
        e.printStackTrace()
        GeminiResult(calorie = "0", suggestion = "No suggestions found.")
    }
}
