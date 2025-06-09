package com.example.nyumbyte.ui.screens.dietplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileproject.ai.GeminiProvider
import com.example.nyumbyte.data.model.DietPlan
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.math.pow

class DietPlanViewModel : ViewModel() {
    private val _structuredDietPlan = MutableStateFlow<List<DietPlan>>(emptyList())
    val structuredDietPlan = _structuredDietPlan.asStateFlow()

    private val _debugMessages = MutableStateFlow<List<String>>(emptyList())
    val debugMessages = _debugMessages.asStateFlow()

    private val _uiState = MutableStateFlow(DietPlanUiState())
    val uiState = _uiState.asStateFlow()

    private val conversationHistory = mutableListOf<Content>()

    fun generateDietPlan(dietConstraints: DietConstraints) {
        viewModelScope.launch {
            println("🔁 Starting diet plan generation")
            _uiState.value = DietPlanUiState(isLoading = true)
            _debugMessages.value = listOf("Generating diet plan... 🥦")

            try {
                val targetWeightLine = if (
                    dietConstraints.goal == "Weight loss" ||
                    dietConstraints.goal == "Muscle gain"
                ) "- Target weight: ${dietConstraints.targetWeight}" else ""

                val bmi = if (dietConstraints.user.height > 0) {
                    dietConstraints.user.weight / ((dietConstraints.user.height / 100.0).pow(2))
                } else 0.0

                val allergiesFormatted = dietConstraints.user.allergies.joinToString(", ").ifBlank { "None" }

                val dietPlanPrompt = """
                    You are a diet assistant. Generate a personalized diet plan in valid **strict JSON array** format only. 
                    
                    ⚠️ Your output must ONLY be a valid JSON array of DietPlan objects (NO text, NO markdown).
                    
                    Each DietPlan object:
                    {
                      "day": "Monday",
                      "meals": [
                        {
                          "time_of_day": "Breakfast",
                          "food_recommended": "Oatmeal with fruits",
                          "food_detail": "Rolled oats with banana and chia seeds",
                          "nutritional_value": "300 kcal, 10g protein, 45g carbs, 7g fat",
                          "preparation": "Boil oats in water or milk and top with fruits"
                        }
                      ]
                    }

                    User data and constraints:
                    - Goal: ${dietConstraints.goal}
                    $targetWeightLine
                    - Duration: ${dietConstraints.targetTime}
                    - Physical activity intensity: ${dietConstraints.physicalIntensity}
                    - Sleep pattern: ${dietConstraints.sleepPattern}
                    - Eating schedule: ${dietConstraints.eatingPattern}
                    - Cooking ability: ${dietConstraints.cookingAbility}
                    - Budget constraints: ${dietConstraints.budgetConstraints}
                    
                    User profile:
                    - Gender: ${dietConstraints.user.gender}
                    - Ethnicity: ${dietConstraints.user.ethnicity}
                    - Age: ${dietConstraints.user.age}
                    - Current weight: ${dietConstraints.user.weight}
                    - Current height: ${dietConstraints.user.height}
                    - Current BMI: ${"%.2f".format(bmi)}
                    - Allergies: $allergiesFormatted
                    - Food dislikes: N/A

                    Output only valid JSON array. No extra text, no formatting.
                """.trimIndent()

                println("📤 Prompt sent to Gemini:")
                println(dietPlanPrompt)

                conversationHistory.add(content(role = "user") { text(dietPlanPrompt) })
                val response = GeminiProvider.model.generateContent(*conversationHistory.toTypedArray())

                val reply = response.text ?: throw Exception("No diet plan received from Gemini.")
                println("🔵 Raw Gemini Response:\n$reply")

                val cleanedReply = cleanGeminiReply(reply)
                println("🟢 Cleaned Gemini Reply:\n$cleanedReply")

                if (!cleanedReply.trim().startsWith("[") || !cleanedReply.trim().endsWith("]")) {
                    throw Exception("Gemini's response is not a valid JSON array. Please check the format.")
                }

                val json = Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                }

                val dietPlans = json.decodeFromString<List<DietPlan>>(cleanedReply)
                println("✅ Decoded DietPlan list with ${dietPlans.size} days")

                conversationHistory.add(content(role = "model") { text(reply) })

                _structuredDietPlan.value = dietPlans
                _uiState.value = DietPlanUiState(
                    dietPlans = dietPlans,
                    isLoading = false,
                    isSuccess = true,
                    isSaved = false,
                    lastUpdated = System.currentTimeMillis()
                )
                _debugMessages.value = listOf("✅ Structured diet plan generated successfully!")
                println("🟢 ViewModel state updated successfully")
            } catch (e: Exception) {
                println("❌ Error: ${e.localizedMessage}")
                println("❌ Stack Trace:\n${e.stackTraceToString()}")

                _uiState.value = DietPlanUiState(
                    isLoading = false,
                    errorMessage = "Error: ${e.localizedMessage}"
                )
                _debugMessages.value = listOf("⚠️ Failed to generate diet plan: ${e.localizedMessage}")
            }
        }
    }

    private fun cleanGeminiReply(raw: String): String {
        return raw
            .replace("```json", "")
            .replace("```", "")
            .replace("json", "")
            .replace("JSON", "")
            .replace("Json", "")
            .trim()
    }
}
