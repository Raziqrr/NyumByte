/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-05-15 16:52:43
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-10 02:01:29
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/model/DietPlan.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DietPlan(
    val day: String,
    val meals: List<Meal>
)
    
@Serializable
data class Meal(
    val time_of_day: String,
    val food_recommended: String,
    val food_detail: String,
    val nutritional_value: String,
    val preparation: String
)

fun DietPlan.toMap(): Map<String, Any> = mapOf(
    "day" to day,
    "meals" to meals.map { it.toMap() }
)

fun dietPlanFromMap(map: Map<String, Any?>): DietPlan = DietPlan(
    day = map["day"] as? String ?: "",
    meals = (map["meals"] as? List<*>)?.mapNotNull {
        (it as? Map<*, *>)?.let { mealMap ->
            @Suppress("UNCHECKED_CAST")
            mealFromMap(mealMap as Map<String, Any?>)
        }
    } ?: emptyList()
)


fun Meal.toMap(): Map<String, Any> = mapOf(
    "time_of_day" to time_of_day,
    "food_recommended" to food_recommended,
    "food_detail" to food_detail,
    "nutritional_value" to nutritional_value,
    "preparation" to preparation
)

fun mealFromMap(map: Map<String, Any?>): Meal = Meal(
    time_of_day = map["time_of_day"] as? String ?: "",
    food_recommended = map["food_recommended"] as? String ?: "",
    food_detail = map["food_detail"] as? String ?: "",
    nutritional_value = map["nutritional_value"] as? String ?: "",
    preparation = map["preparation"] as? String ?: ""
)
