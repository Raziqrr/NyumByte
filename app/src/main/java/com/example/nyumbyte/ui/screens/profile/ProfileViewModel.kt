package com.example.nyumbyte.ui.screens.profile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nyumbyte.data.network.firebase.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.example.nyumbyte.data.model.Achievement
import com.example.nyumbyte.data.model.Challenge
import com.example.nyumbyte.data.model.DailyChallenge
import com.example.nyumbyte.data.repository.AchievementRepository
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProfileViewModel : ViewModel() {

    private val _streakCount = MutableStateFlow(0)
    val streakCount: StateFlow<Int> = _streakCount

    private val _lastCompletedDate = MutableStateFlow("")
    val lastCompletedDate: StateFlow<String> = _lastCompletedDate


    private val _dailyChallenges = MutableStateFlow<List<DailyChallenge>>(emptyList())
    val dailyChallenges: StateFlow<List<DailyChallenge>> = _dailyChallenges
    private val levelThresholds = listOf(0, 100, 250, 500, 1000, 2000)
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements


    private val _userName = MutableStateFlow<String>("")
    val userName: StateFlow<String> = _userName

    private val _userXp = MutableStateFlow(0)
    val userXp: StateFlow<Int> = _userXp

    private val _userLevel = MutableStateFlow(1)
    val userLevel: StateFlow<Int> = _userLevel

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadStreakData(uid: String) {
        viewModelScope.launch {
            try {
                val data = FirestoreRepository.getUserData(uid)
                _streakCount.value = (data?.get("streakCount") as? Long)?.toInt() ?: 0
                _lastCompletedDate.value = data?.get("lastCompletedDate") as? String ?: ""
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to load streak data", e)
            }
        }
    }


    private fun getYesterdayDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    fun updateStreakIfNeeded(uid: String) {
        viewModelScope.launch {
            try {
                val data = FirestoreRepository.getUserData(uid)
                val lastDate = data?.get("lastCompletedDate") as? String ?: ""
                val currentStreak = (data?.get("streakCount") as? Long)?.toInt() ?: 0

                val today = getTodayDate()
                val yesterday = getYesterdayDate()

                if (lastDate == today) return@launch // already updated today

                val newStreak = when (lastDate) {
                    yesterday -> currentStreak + 1
                    else -> 1
                }

                _streakCount.value = newStreak
                _lastCompletedDate.value = today

                FirestoreRepository.updateUserField(uid, "streakCount", newStreak)
                FirestoreRepository.updateUserField(uid, "lastCompletedDate", today)

                Log.d("StreakUpdate", "Updated to $newStreak on $today")

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating streak", e)
            }
        }
    }


    fun resetStreak(uid: String) {
        val oldDate = getYesterdayDate() // or you can use getYesterdayDate() if you want
        _streakCount.value = 1
        _lastCompletedDate.value = oldDate

        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .document(uid)
                .update(
                    mapOf(
                        "streakCount" to 1,
                        "lastCompletedDate" to oldDate
                    )
                )
        }
    }


    fun testAddAchievement(uid: String) {
        viewModelScope.launch {
            val testId = "test_achievement_manual"
            val achievementData = mapOf(
                "title" to "Test Achievement",
                "description" to "Manually added for testing display.",
                "date" to getTodayDate(),
                "level" to 1,
                "type" to "manual"
            )
            AchievementRepository.saveAchievement(uid, testId, achievementData)
            loadAchievements(uid) // Refresh the list after adding
        }
    }

    fun loadDailyChallenges(date: String, uid: String) {
        viewModelScope.launch {
            try {
                Log.d("ProfileVM", "Loading daily challenges for date=$date and uid=$uid")
                val challenges = FirestoreRepository.getDailyChallengesWithStatus(date, uid)
                Log.d("ProfileVM", "Loaded ${challenges.size} challenges")
                _dailyChallenges.value = challenges
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to load daily challenges", e)
            }
        }
    }


    fun completeDailyChallenge(uid: String, challenge: DailyChallenge, date: String) {
        viewModelScope.launch {
            try {
                // 1. Optimistic UI update
                _dailyChallenges.value = _dailyChallenges.value.map {
                    if (it.id == challenge.id) it.copy(isCompleted = true) else it
                }

                // 2. Mark challenge as completed in Firestore
                FirestoreRepository.markDailyChallengeCompleted(uid, date, challenge.id)

                // 3. Add XP and calculate new level
                val previousLevel = _userLevel.value
                val newXp = _userXp.value + challenge.expReward
                val newLevel = calculateLevel(newXp)


                _userXp.value = newXp
                _userLevel.value = newLevel

                // 4. ✅ Save XP and level to Firestore
                FirestoreRepository.updateUserField(uid, "exp", newXp)
                FirestoreRepository.updateUserField(uid, "level", newLevel)
                if (newLevel > previousLevel) {
                    val achievementId = "level_$newLevel"
                    val achievementData = mapOf(
                        "title" to "Leveled Up",
                        "description" to "Reached Level $newLevel by earning $newXp XP.",
                        "date" to getTodayDate(),  // or manually "2025-06-20"
                        "level" to newLevel,
                        "type" to "level_up"
                    )
                    AchievementRepository.saveAchievement(uid, achievementId, achievementData)
                    loadAchievements(uid)

                }
                updateStreakIfNeeded(uid)

                // 5. Optional reload of challenges
                loadDailyChallenges(date, uid)

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error completing daily challenge", e)
            }
        }
    }




    fun loadUserData(uid: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val data = FirestoreRepository.getUserData(uid)

                _userName.value = data?.get("userName") as? String ?: "Unknown"
                val xp = (data?.get("exp") as? Long)?.toInt() ?: 0
                _userXp.value = xp
                _userLevel.value = calculateLevel(xp)

                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error loading profile."
            } finally {
                _loading.value = false
            }
        }
    }


    fun updateUserName(uid: String, newName: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val success = FirestoreRepository.updateUserField(uid, "userName", newName)
                if (success) {
                    _userName.value = newName
                    _error.value = null
                } else {
                    _error.value = "Failed to update username."
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating username", e)
                _error.value = "Error updating username."
            } finally {
                _loading.value = false
            }
        }
    }
    fun updateXp(uid: String, newXp: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val success = FirestoreRepository.updateUserField(uid, "xp", newXp)
                if (success) {
                    _userXp.value = newXp
                    _userLevel.value = calculateLevel(newXp)
                    _error.value = null
                } else {
                    _error.value = "Failed to update XP."
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating XP", e)
                _error.value = "Error updating XP."
            } finally {
                _loading.value = false
            }
        }
    }
    private fun getTodayDate(): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
    }

    private fun calculateLevel(xp: Int): Int {
        var level = 1
        for (i in 1 until levelThresholds.size) {
            if (xp >= levelThresholds[i]) {
                level = i + 1
            } else {
                break
            }
        }
        return level
    }

    fun loadAchievements(uid: String) {
        viewModelScope.launch {
            try {
                val docs = AchievementRepository.getAchievements(uid)
                _achievements.value = docs

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading achievements", e)
            }
        }
    }
}

