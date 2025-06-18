package com.example.nyumbyte.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nyumbyte.R

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.nyumbyte.ui.screens.challenges.ChallengeRepository.getLevelIcon

data class FriendProfile(
    val id: String = "",
    val userName: String = "",
    val level: Int = 1
)

@Composable
fun SocialPage(userId: String) {
    val db = Firebase.firestore
    var friendIdInput by remember { mutableStateOf(TextFieldValue("")) }
    var friendList by remember { mutableStateOf<List<FriendProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    suspend fun fetchFriends(friendIds: List<String>) {
        val friends = mutableListOf<FriendProfile>()
        for (id in friendIds) {
            val doc = db.collection("Users").document(id).get().await()
            if (doc.exists()) {
                val userName = doc.getString("userName") ?: "Unknown"
                val level = (doc.getLong("level") ?: 1).toInt()
                friends.add(FriendProfile(id = id, userName = userName, level = level))
            }
        }
        friendList = friends
    }

    LaunchedEffect(userId) {
        val snapshot = db.collection("Users").document(userId).get().await()
        val ids = snapshot.get("friends") as? List<String> ?: emptyList()
        fetchFriends(ids)
        isLoading = false
    }

    fun addFriend() {
        coroutineScope.launch {
            val input = friendIdInput.text.trim()
            if (input.isEmpty()) return@launch

            // Try by username
            val query = db.collection("Users")
                .whereEqualTo("userName", input)
                .get().await()

            val friendDoc = when {
                query.documents.isNotEmpty() -> query.documents.first()
                else -> {
                    val byId = db.collection("Users").document(input).get().await()
                    if (byId.exists()) byId else null
                }
            }

            if (friendDoc != null) {
                val friendId = friendDoc.id
                if (friendList.none { it.id == friendId } && friendId != userId) {
                    val currentSnapshot = db.collection("Users").document(userId).get().await()
                    val currentFriends = currentSnapshot.get("friends") as? List<String> ?: emptyList()
                    val updatedFriends = currentFriends + friendId

                    db.collection("Users").document(userId).update("friends", updatedFriends).await()
                    fetchFriends(updatedFriends)
                    friendIdInput = TextFieldValue("")
                    errorMsg = null
                } else {
                    errorMsg = "Friend already added or invalid."
                }
            } else {
                errorMsg = "User not found."
            }
        }
    }

    fun removeFriend(friendId: String) {
        coroutineScope.launch {
            val updatedFriends = friendList.map { it.id }.filterNot { it == friendId }
            db.collection("Users").document(userId).update("friends", updatedFriends).await()
            fetchFriends(updatedFriends)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .padding(16.dp)
    ) {
        Text("Your Friends:", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = friendIdInput,
            onValueChange = { friendIdInput = it },
            label = { Text("Enter friend's username or ID", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { addFriend() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Friend")
        }

        errorMsg?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color.Red, fontSize = 14.sp)
        }

        Spacer(Modifier.height(24.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            LazyColumn {
                items(friendList) { friend ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = getLevelIcon(friend.level)),
                                    contentDescription = "Avatar Icon",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text("Name: ${friend.userName}", color = Color.White, fontWeight = FontWeight.SemiBold)
                                    Text("Level: ${friend.level}", color = Color.Gray, fontSize = 14.sp)
                                    Text("ID: ${friend.id}", color = Color.LightGray, fontSize = 12.sp)
                                }
                            }

                            IconButton(onClick = { removeFriend(friend.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Friend",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
