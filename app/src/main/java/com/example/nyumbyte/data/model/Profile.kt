package com.example.nyumbyte.data.model

data class Profile(
    val userId: String = "",
    val completedChallenges: List<String> = emptyList(),
    val dailyStreak: Int = 0,
    val badges: List<String> = emptyList(),
    val appHistory: List<String> = emptyList(),
    val rewards: List<String> = emptyList()
){
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "dailyStreak" to dailyStreak,
            "badges" to badges,
            "completedChallenges" to completedChallenges,
            "appHistory" to appHistory,
            "rewardsClaimed" to rewards
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): Profile {
            return Profile(
                userId = map["userId"] as? String ?: "",
                dailyStreak = (map["dailyStreak"] as? Number)?.toInt() ?: 0,
                badges = (map["badges"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                completedChallenges = (map["completedChallenges"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                appHistory = (map["appHistory"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                rewards = (map["rewardsClaimed"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            )
        }
    }
}
