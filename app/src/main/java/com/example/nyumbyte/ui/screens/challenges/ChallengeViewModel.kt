package com.example.nyumbyte.ui.screens.challenges

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nyumbyte.R
import com.example.nyumbyte.data.model.Challenge
import com.example.nyumbyte.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChallengeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val userId: String get() = auth.currentUser?.uid.orEmpty()

    var user by mutableStateOf<User?>(null)
        private set

    var avatarRes by mutableStateOf(R.drawable.default_avatar)
        private set

    var challenges by mutableStateOf<List<Challenge>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var showExpPopup by mutableStateOf(false)
    var showPointsPopup by mutableStateOf(false)

    fun loadInitialData() {
        viewModelScope.launch {
            isLoading = true
            fetchUserData()
            fetchChallenges()
            isLoading = false
        }
    }

    private fun fetchUserData() {
        if (userId.isEmpty()) return

        db.collection("Users").document(userId).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                val userMap = snapshot.data ?: return@addSnapshotListener
                val parsedUser = User.fromMap(userMap)
                user = parsedUser
                avatarRes = ChallengeRepository.getLevelIcon(parsedUser.level)
            }
        }
    }

    private suspend fun fetchChallenges() {
        try {
            val snapshot = db.collection("challenges").get().await()
            challenges = snapshot.documents.mapNotNull { doc ->
                val challenge = doc.toObject(Challenge::class.java)
                val completedMap = doc.get("completedBy") as? Map<String, Boolean> ?: emptyMap()

                challenge?.copy(
                    docId = doc.id,
                    completed = completedMap[userId] == true
                )
            }
        } catch (e: Exception) {
            println("Error fetching challenges: ${e.localizedMessage}")
        }
    }

    fun completeChallenge(challenge: Challenge) {
        if (userId.isEmpty() || challenge.completed) return

        viewModelScope.launch {
            try {
                val challengeRef = db.collection("challenges").document(challenge.docId)
                val snapshot = challengeRef.get().await()
                val completedMap = snapshot.get("completedBy") as? Map<String, Boolean> ?: emptyMap()

                if (!completedMap.containsKey(userId)) {
                    // ✅ Mark as completed
                    challengeRef.update("completedBy.$userId", true).await()

                    // ✅ Reward user
                    ChallengeRepository.addExp(userId, challenge.expReward, challenge.category)

                    // ✅ Show popup
                    showExpPopup = true
                    showPointsPopup = true

                    delay(2000)
                    showExpPopup = false
                    showPointsPopup = false

                    // ✅ Refresh list
                    fetchChallenges()
                }
            } catch (e: Exception) {
                println("Error completing challenge: ${e.localizedMessage}")
            }
        }
    }
}
