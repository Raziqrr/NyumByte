package com.example.nyumbyte.ui.screens.profile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nyumbyte.data.network.firebase.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.example.nyumbyte.data.model.Challenge
import com.example.nyumbyte.data.model.DailyChallenge

class ProfileViewModel : ViewModel() {
    private val _dailyChallenges = MutableStateFlow<List<DailyChallenge>>(emptyList())
    val dailyChallenges: StateFlow<List<DailyChallenge>> = _dailyChallenges
    private val levelThresholds = listOf(0, 100, 250, 500, 1000, 2000)


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
                val newXp = _userXp.value + challenge.expReward
                val newLevel = calculateLevel(newXp)

                _userXp.value = newXp
                _userLevel.value = newLevel

                // 4. ✅ Save XP and level to Firestore
                FirestoreRepository.updateUserField(uid, "exp", newXp)
                FirestoreRepository.updateUserField(uid, "level", newLevel)

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




}

