package com.example.nyumbyte.ui.screens.social.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nyumbyte.ui.screens.social.Story
import com.example.nyumbyte.ui.screens.social.FriendProfile

import com.example.nyumbyte.ui.screens.social.friend.FriendCard
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

@Composable
fun SocialFeed(userId: String) {
    val db = Firebase.firestore
    val storage = FirebaseStorage.getInstance()
    val scope = rememberCoroutineScope()

    var friendIdInput by remember { mutableStateOf("") }
    var friendList by remember { mutableStateOf<List<FriendProfile>>(emptyList()) }
    var stories by remember { mutableStateOf<List<Story>>(emptyList()) }
    var showViewer by remember { mutableStateOf<List<Story>?>(null) }
    var currentStoryIndex by remember { mutableStateOf(0) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var showRemoveConfirm by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        selectedImageUri = it
        if (it != null) showUploadDialog = true
    }

    fun fetchFriends(ids: List<String>) {
        scope.launch {
            val list = ids.mapNotNull { id ->
                val doc = db.collection("Users").document(id).get().await()
                if (doc.exists()) {
                    FriendProfile(id, doc.getString("userName") ?: "", (doc.getLong("level") ?: 1).toInt())
                } else null
            }
            friendList = list
        }
    }

    fun addFriend() {
        scope.launch {
            try {
                val input = friendIdInput.trim()
                if (input.isEmpty()) return@launch

                val query = db.collection("Users").whereEqualTo("userName", input).get().await()
                val friendDoc = if (query.documents.isNotEmpty()) query.documents.first()
                else db.collection("Users").document(input).get().takeIf { it.await().exists() }?.await()

                if (friendDoc != null) {
                    val id = friendDoc.id
                    if (id != userId && friendList.none { it.id == id }) {
                        val current = db.collection("Users").document(userId).get().await()
                        val currentFriends = current.get("friends") as? List<String> ?: emptyList()
                        val updated = currentFriends + id
                        db.collection("Users").document(userId).update("friends", updated).await()
                        fetchFriends(updated)
                        friendIdInput = ""
                        errorMsg = null
                    } else errorMsg = "Friend already added or invalid."
                } else errorMsg = "User not found."
            } catch (e: Exception) {
                errorMsg = "Error adding friend."
            }
        }
    }

    fun removeFriend(id: String) {
        scope.launch {
            val updated = friendList.map { it.id }.filterNot { it == id }
            db.collection("Users").document(userId).update("friends", updated).await()
            fetchFriends(updated)
            showRemoveConfirm = null
        }
    }

    LaunchedEffect(userId) {
        val snap = db.collection("Users").document(userId).get().await()
        val ids = snap.get("friends") as? List<String> ?: emptyList()
        fetchFriends(ids)

        val allIds = listOf(userId) + ids
        val query = db.collection("StatusPosts")
            .whereIn("userId", allIds)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get().await()

        stories = query.documents.mapNotNull {
            val uid = it.getString("userId") ?: return@mapNotNull null
            val name = it.getString("userName") ?: "Unknown"
            val url = it.getString("imageUrl") ?: return@mapNotNull null
            val desc = it.getString("description") ?: ""
            Story(uid, name, url, desc)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Social", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                    }
                    Text("Your Story", fontSize = 12.sp)
                }
            }
            items(stories.distinctBy { it.userId }) { story ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = story.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .clickable {
                                val userStories = stories.filter { it.userId == story.userId }
                                showViewer = userStories
                                currentStoryIndex = 0
                            }
                    )
                    Text(story.userName, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = friendIdInput,
            onValueChange = { friendIdInput = it },
            label = { Text("Enter friend ID or username") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { addFriend() }, modifier = Modifier.align(Alignment.End)) {
            Text("Add Friend")
        }

        errorMsg?.let {
            Text(it, color = Color.Red)
        }

        Spacer(Modifier.height(12.dp))

        FriendCard(
            friendList = friendList,
            onRemoveRequest = { showRemoveConfirm = it }
        )
    }

    if (showViewer != null) {
        StoryViewer(
            stories = showViewer!!,
            startIndex = currentStoryIndex,
            onClose = { showViewer = null }
        )
    }

    showRemoveConfirm?.let {
        AlertDialog(
            onDismissRequest = { showRemoveConfirm = null },
            title = { Text("Remove Friend") },
            text = { Text("Are you sure you want to remove this friend?") },
            confirmButton = {
                TextButton(onClick = { removeFriend(it) }) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveConfirm = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showUploadDialog && selectedImageUri != null) {
        var desc by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {
                showUploadDialog = false
                selectedImageUri = null
            },
            title = { Text("Upload Story") },
            text = {
                TextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        try {
                            val filename = UUID.randomUUID().toString()
                            val ref = storage.reference.child("stories/$filename")
                            ref.putFile(selectedImageUri!!).await()
                            val url = ref.downloadUrl.await().toString()

                            val userDoc = db.collection("Users").document(userId).get().await()
                            val userName = userDoc.getString("userName") ?: "Unknown"

                            db.collection("StatusPosts").add(
                                mapOf(
                                    "userId" to userId,
                                    "userName" to userName,
                                    "description" to desc,
                                    "imageUrl" to url,
                                    "timestamp" to System.currentTimeMillis()
                                )
                            )
                            showUploadDialog = false
                            selectedImageUri = null
                        } catch (e: Exception) {
                            Log.e("UploadError", e.message ?: "Upload failed")
                        }
                    }
                }) {
                    Text("Upload")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showUploadDialog = false
                    selectedImageUri = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
