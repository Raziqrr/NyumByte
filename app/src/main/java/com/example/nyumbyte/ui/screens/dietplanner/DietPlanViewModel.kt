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

    fun markPlanAsSaved() {
        _uiState.value = _uiState.value.copy(isSaved = true)
        _debugMessages.value = listOf("💾 Diet plan marked as saved.")
    }
    fun resetState() {
        _structuredDietPlan.value = emptyList()
        _debugMessages.value = emptyList()
        _uiState.value = DietPlanUiState() // or whatever your default is
        conversationHistory.clear()
    }


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
                    You are a professional dietician AI. Generate a personalized diet plan in valid **strict JSON array** format only. 
                    
                    Think of a diet plan that can fully achieve the goal successfully and within the constraints if given.
                    Each day should include 3 main meals (breakfast, lunch, dinner) and optionally 1–2 more meals which is based on the goal. Ensure:
                    - Total daily calories support the user goal (e.g., deficit for weight loss)
                    - Macronutrient distribution is healthy and goal-aligned
                    - Meals are realistic for user skill level and budget
                    - Meals consider allergies and constraints
                    - Preparation steps are clear and ingredients are listed
                    
                    
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
                          "ingredients": [
                            "1/2 cup rolled oats",
                            "1 banana",
                            "1 tsp chia seeds",
                            "1 cup water or milk"
                          ],
                          "preparation": "In a small pot, bring 1 cup of water or milk to a boil. Add 1/2 cup of rolled oats and reduce heat to low. Simmer for 5 minutes, stirring occasionally, until thickened. Slice 1 banana and top the oatmeal with banana slices and 1 tsp chia seeds. Serve warm."
                        }
                      ]
                    }

                    User data and constraints:
                    - Meal Plan Goal: ${dietConstraints.goal}
                    $targetWeightLine
                    - Goal can be achieved in: ${dietConstraints.targetTime}
                    - User's Physical activity intensity: ${dietConstraints.physicalIntensity}
                    - User's Sleep pattern: ${dietConstraints.sleepPattern}
                    - User's Daily Eating schedule: ${dietConstraints.eatingPattern}
                    - User's Cooking ability: ${dietConstraints.cookingAbility}
                    - User's Budget constraints: ${dietConstraints.budgetConstraints}
                    
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
