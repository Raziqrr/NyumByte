/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-05-15 10:45:25
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-10 00:56:45
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/dietplanner/DietConstraints.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.dietplanner

import com.example.nyumbyte.data.model.User

val dietGoalOptions = listOf(
    "Weight loss",          // Requires: current weight, target weight, target time, activity level, calorie preference
    "Muscle gain",          // Requires: current weight, target weight/muscle gain goal, protein preference, workout intensity
    "Maintenance",          // Requires: current weight, typical activity level, eating habits
    "Better energy levels", // Requires: sleep pattern, meal timing, current diet variety, fatigue symptoms
    "Heart health",         // Requires: cholesterol levels (optional), fat/sodium intake, family history, stress
    "Diabetes management",  // Requires: blood sugar tracking, carb intake, meal timing, medication (if any)
    "General wellness"      // Requires: balanced nutrition, activity level, sleep, hydration habits
)

// 1. Target Duration
val targetDurationOptions = listOf(
    "1–2 weeks (short-term)",
    "1 month",
    "3 months",
    "6 months",
    "12 months (long-term)",
    "Custom date"
)

// 2. Physical Activity Level
val physicalActivityOptions = listOf(
    "Sedentary (little or no exercise)",
    "Lightly active (1–3 days/week)",
    "Moderately active (3–5 days/week)",
    "Very active (6–7 days/week)",
    "Extra active (daily training or physical job)"
)

// 3. Sleep Pattern
val sleepPatternOptions = listOf(
    "Less than 5 hours/night",
    "5–6 hours/night",
    "6–7 hours/night",
    "7–8 hours/night (optimal)",
    "More than 8 hours/night",
    "Irregular schedule (e.g., shift work)"
)

// 4. Cooking Skill Level
val cookingSkillOptions = listOf(
    "None (relies on ready meals/takeout)",
    "Basic (simple prep: boiling, frying, steaming)",
    "Intermediate (can follow recipes confidently)",
    "Advanced (meal prep, multitasking, variety)",
    "Professional (trained or experienced cook)"
)

// 5. Budget Range
val budgetOptions = listOf(
    "Minimal (< $5/day)",
    "Moderate ($5–10/day)",
    "Flexible (> $10/day)",
    "Premium (organic or specialty items)"
)

class DietConstraints(val user: User) {
    var goal: String = ""
    var targetTime: String = ""
    var physicalIntensity: String = ""
    var sleepPattern: Int = 0
    var eatingPattern: Int = 0
    var cookingAbility: String = ""
    var budgetConstraints: String = ""
    var targetWeight: Double = 0.0
}