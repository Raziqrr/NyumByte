package com.example.nyumbyte.ui.screens.health


import android.util.Log
import com.example.nyumbyte.data.network.gemini.GeminiApi
import com.example.nyumbyte.ui.screens.health.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiRepository {
    private const val API_KEY = "AIzaSyAZlFNUBS2898eUjh8JjCK9Jb8bE8xoIrQ"

    suspend fun analyzeHealthPrompt(prompt: String): String? = withContext(Dispatchers.IO) {
        try {
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = prompt))
                    )
                )
            )

            val response = GeminiApi.retrofitService.analyzeHealth(request, API_KEY).execute()
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("GeminiAPI", "Response body: $body") // ADD THIS
                return@withContext body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            } else {
                Log.e("GeminiAPI", "Error code: ${response.code()}")
                Log.e("GeminiAPI", "Error body: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("GeminiAPI", "Error: ${e.message}")
            null
        }
    }
}
