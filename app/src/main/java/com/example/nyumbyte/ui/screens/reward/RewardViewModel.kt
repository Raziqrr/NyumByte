package com.example.nyumbyte.ui.screens.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RewardViewModel(private val userId: String) : ViewModel() {
    private val db = Firebase.firestore
    private val userRef = db.collection("Users").document(userId)

    private val _coinBalance = MutableStateFlow(0)
    val coinBalance: StateFlow<Int> = _coinBalance

    private val _claimedBadges = MutableStateFlow<List<String>>(emptyList())
    val claimedBadges: StateFlow<List<String>> = _claimedBadges

    private val _claimedMerch = MutableStateFlow<List<String>>(emptyList())
    val claimedMerch: StateFlow<List<String>> = _claimedMerch

    init {
        viewModelScope.launch {
            fetchUserData()
        }
    }

    private suspend fun fetchUserData() {
        try {
            val snapshot = userRef.get().await()
            _coinBalance.value = snapshot.getLong("totalPoints")?.toInt() ?: 0
            _claimedBadges.value = snapshot.get("badge") as? List<String> ?: emptyList()
            _claimedMerch.value = snapshot.get("merch") as? List<String> ?: emptyList()
        } catch (e: Exception) {
            println("Error fetching user data: ${e.localizedMessage}")
        }
    }

    fun purchaseBadge(name: String) {
        viewModelScope.launch {
            try {
                val snapshot = userRef.get().await()
                val currentCoins = snapshot.getLong("totalPoints")?.toInt() ?: 0
                val currentBadges = snapshot.get("badge") as? List<String> ?: emptyList()

                if (!currentBadges.contains(name) && currentCoins >= 25) {
                    userRef.update(
                        "totalPoints", currentCoins - 25,
                        "badge", currentBadges + name
                    ).await()

                    _coinBalance.value = currentCoins - 25
                    _claimedBadges.value = currentBadges + name
                }
            } catch (e: Exception) {
                println("Error purchasing badge: ${e.localizedMessage}")
            }
        }
    }

    fun purchaseMerch(name: String) {
        viewModelScope.launch {
            try {
                val snapshot = userRef.get().await()
                val currentCoins = snapshot.getLong("totalPoints")?.toInt() ?: 0
                val currentMerch = snapshot.get("merch") as? List<String> ?: emptyList()

                val merchCost = when (name) {
                    "Shirt" -> 800
                    "Cap" -> 700
                    "Tumbler" -> 350
                    else -> return@launch
                }

                if (!currentMerch.contains(name) && currentCoins >= merchCost) {
                    userRef.update(
                        "totalPoints", currentCoins - merchCost,
                        "merch", currentMerch + name
                    ).await()

                    _coinBalance.value = currentCoins - merchCost
                    _claimedMerch.value = currentMerch + name
                }
            } catch (e: Exception) {
                println("Error purchasing merch: ${e.localizedMessage}")
            }
        }
    }
}
