package com.example.nyumbyte.ui.status

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class FriendStatus(
    val userId: String = "",
    val username: String = "",
    val activity: String = "",
    val emoji: String = ""
)

class StatusViewModel : ViewModel() {
    var friendStatuses by mutableStateOf<List<FriendStatus>>(emptyList())
        private set

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        loadFriendStatuses()
    }

    private fun loadFriendStatuses() {
        val currentUserId = auth.currentUser?.uid ?: return
        val userRef = db.collection("Users").document(currentUserId)

        userRef.get().addOnSuccessListener { doc ->
            val friendIds = doc.get("friends") as? List<String> ?: emptyList()

            if (friendIds.isEmpty()) return@addOnSuccessListener

            db.collection("Statuses").whereIn("userId", friendIds)
                .get()
                .addOnSuccessListener { snapshot ->
                    val friends = snapshot.documents.mapNotNull { doc ->
                        val userId = doc.getString("userId") ?: return@mapNotNull null
                        val username = doc.getString("username") ?: "Unknown"
                        val activity = doc.getString("activity") ?: "Unknown"
                        val emoji = doc.getString("emoji") ?: ""
                        FriendStatus(userId, username, activity, emoji)
                    }
                    friendStatuses = friends
                }
        }
    }
}

@Composable
fun StatusPage() {
    val viewModel: StatusViewModel = viewModel()
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Friends’ Current Locations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.friendStatuses.size) { index ->
                val friend = viewModel.friendStatuses[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${friend.username} ${friend.emoji}", style = MaterialTheme.typography.titleMedium)
                        Text(friend.activity, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/yourusername/yourmaprepo"))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔗 View Friends on GitHub Map")
        }
    }
}
