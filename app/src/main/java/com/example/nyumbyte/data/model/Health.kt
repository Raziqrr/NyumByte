package com.example.nyumbyte.data.model

// data/model/Health.kt
data class Health(
    val week: String = "",
    val calorieIntake: Map<String, Int> = emptyMap(), // e.g., "Monday" -> 2200
    val waterIntake: Map<String, Int> = emptyMap()    // e.g., "Monday" -> 3000 ml
) {
    fun toMap(): Map<String, Any> = mapOf(
        "week" to week,
        "calorieIntake" to calorieIntake,
        "waterIntake" to waterIntake
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): Health {
            return Health(
                week = map["week"] as? String ?: "",
                calorieIntake = (map["calorieIntake"] as? Map<*, *>)?.mapNotNull {
                    val key = it.key as? String
                    val value = (it.value as? Number)?.toInt()
                    if (key != null && value != null) key to value else null
                }?.toMap() ?: emptyMap(),

                waterIntake = (map["waterIntake"] as? Map<*, *>)?.mapNotNull {
                    val key = it.key as? String
                    val value = (it.value as? Number)?.toInt()
                    if (key != null && value != null) key to value else null
                }?.toMap() ?: emptyMap()
            )
        }
    }
}
