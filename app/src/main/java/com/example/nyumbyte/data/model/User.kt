/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 01:12:09
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-08 00:19:35
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/model/User.kt
 * @Description: Improved data model with safe conversion
 */
package com.example.nyumbyte.data.model

import androidx.room.PrimaryKey

data class User(
    @PrimaryKey val id: String,
    val userName: String,
    var age: Int,
    var phoneNumber: String,

    var weight: Double,
    var height: Double,

    var allergies: List<String>,
    val gender: String,
    val ethnicity: String,

    var level: Int,
    var exp: Int,

    val totalPoints: Int,
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userName" to userName,
            "age" to age,
            "phoneNumber" to phoneNumber,
            "weight" to weight,
            "height" to height,
            "allergies" to allergies,
            "gender" to gender,
            "ethnicity" to ethnicity,
            "level" to level,
            "exp" to exp,
            "totalPoints" to totalPoints
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): User {
            return User(
                id = map["id"] as? String ?: "",
                userName = map["userName"] as? String ?: "",
                age = (map["age"] as? Number)?.toInt() ?: 0,
                phoneNumber = map["phoneNumber"] as? String ?: "",
                weight = (map["weight"] as? Number)?.toDouble() ?: 0.0,
                height = (map["height"] as? Number)?.toDouble() ?: 0.0,
                allergies = (map["allergies"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                gender = map["gender"] as? String ?: "",
                ethnicity = map["ethnicity"] as? String ?: "",
                level = (map["level"] as? Number)?.toInt() ?: 1,
                exp = (map["exp"] as? Number)?.toInt() ?: 0,
                totalPoints = (map["totalPoints"] as? Number)?.toInt() ?: 0
            )
        }
    }
}
