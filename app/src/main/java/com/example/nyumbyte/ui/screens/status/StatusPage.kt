package com.example.nyumbyte.ui.screens.status

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

data class StoryPost(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0
)

@Composable
fun StatusPage(userId: String) {
    val db = Firebase.firestore
    val storage = Firebase.storage
    val coroutineScope = rememberCoroutineScope()

    var storyUri by remember { mutableStateOf<Uri?>(null) }
    var postList by remember { mutableStateOf<List<StoryPost>>(emptyList()) }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        storyUri = it
    }

    fun fetchPosts() {
        coroutineScope.launch {
            val userDoc = db.collection("Users").document(userId).get().await()
            val friends = userDoc.get("friends") as? List<String> ?: emptyList()
            val allowedIds = friends + userId

            if (allowedIds.isNotEmpty()) {
                val chunks = allowedIds.chunked(10) // Firestore whereIn supports max 10
                val allResults = chunks.map { chunk ->
                    db.collection("StatusPosts")
                        .whereIn("userId", chunk)
                        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .get().await()
                }

                postList = allResults.flatMap { result ->
                    result.documents.map { doc ->
                        StoryPost(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            userName = doc.getString("userName") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    }
                }.sortedByDescending { it.timestamp }
            }
        }
    }

    fun uploadStory() {
        coroutineScope.launch {
            storyUri?.let { uri ->
                isUploading = true
                val fileName = UUID.randomUUID().toString()
                val ref = storage.reference.child("stories/$fileName.jpg")
                ref.putFile(uri).await()
                val downloadUrl = ref.downloadUrl.await().toString()

                val userDoc = db.collection("Users").document(userId).get().await()
                val username = userDoc.getString("userName") ?: "Unknown"

                db.collection("StatusPosts").add(
                    mapOf(
                        "userId" to userId,
                        "userName" to username,
                        "imageUrl" to downloadUrl,
                        "timestamp" to System.currentTimeMillis()
                    )
                )
                storyUri = null
                isUploading = false
                fetchPosts()
            }
        }
    }

    LaunchedEffect(userId) {
        fetchPosts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Status / Stories",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { launcher.launch("image/*") }) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Choose")
                Spacer(Modifier.width(8.dp))
                Text("Choose Photo")
            }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = { uploadStory() },
                enabled = storyUri != null && !isUploading
            ) {
                Text("Upload Story")
            }
        }

        if (isUploading) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(postList) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Posted by: ${post.userName}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = rememberAsyncImagePainter(post.imageUrl),
                            contentDescription = "Story Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }
        }
    }
}
