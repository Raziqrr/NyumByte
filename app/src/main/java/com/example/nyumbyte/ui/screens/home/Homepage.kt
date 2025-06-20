/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-08 01:41:59
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-20 10:11:31
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/home/Homepage.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.home

import PrimaryButton
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.nyumbyte.R
import com.example.nyumbyte.data.model.Meal

import com.example.nyumbyte.data.network.firebase.UserUiState
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.beta.LiquidGlass
import com.example.nyumbyte.ui.beta.LiquidGlassConfig
import com.example.nyumbyte.ui.beta.LiquidGlassDemo
import com.example.nyumbyte.ui.beta.LiquidGlassText
import com.example.nyumbyte.ui.common.SecondaryButton
import com.example.nyumbyte.ui.common.StoryRing
import com.example.nyumbyte.ui.navigation.Screens
import com.example.nyumbyte.ui.screens.social.FriendProfile
import com.example.nyumbyte.ui.screens.social.Story
import com.example.nyumbyte.ui.screens.social.components.StoryViewer
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Homepage(
    userViewModel: UserViewModel,
    navController: NavHostController
){
    val userState by userViewModel.userUiState.collectAsState()
    val user = (userState as? UserUiState.Success)?.user
    val dietPlans = user?.dietPlan ?: emptyList()
    val uid = (userState as? UserUiState.Success)?.user?.id ?: return
    val todayName = remember {
        LocalDate.now().dayOfWeek.name.lowercase()
            .replaceFirstChar { it.uppercase() }
    } // e.g., "Thursday"


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
            val db = Firebase.firestore
            val list = ids.mapNotNull { id ->
                val doc = db.collection("Users").document(id).get().await()
                val userName = doc.getString("userName") ?: "Unknown"
                val level = doc.getLong("level") ?: 1
                Log.d("FriendFetch", "ID: $id | Name: $userName | Level: $level")
                if (doc.exists()) {
                    FriendProfile(id, userName, level.toInt())
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
                    if (id != uid && friendList.none { it.id == id }) {
                        val current = db.collection("Users").document(uid).get().await()
                        val currentFriends = current.get("friends") as? List<String> ?: emptyList()
                        val updated = currentFriends + id
                        db.collection("Users").document(uid).update("friends", updated).await()
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
            db.collection("Users").document(uid).update("friends", updated).await()
            fetchFriends(updated)
            showRemoveConfirm = null
        }
    }

    LaunchedEffect(uid) {
        val snap = db.collection("Users").document(uid).get().await()
        val ids = snap.get("friends") as? List<String> ?: emptyList()
        fetchFriends(ids)

        val allIds = listOf(uid) + ids
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
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Card { 
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Today's Calorie Count"
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (user != null) {
                    Text(
                        "${user.calorieToday}",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                    ) {
                    SecondaryButton(
                        text = "Go To Challenges",
                        onClick = { navController.navigate(Screens.ChallengePage.name) },
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButton(
                        text = "Manage Health",
                        onClick = { navController.navigate(Screens.Health.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        Spacer(
            Modifier.height(20.dp)
        )
        Spacer(
            Modifier.height(20.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Stories"
                    )
                    TextButton(onClick = {
                        navController.navigate(Screens.SocialPage.name)
                    }) {
                        Text("View Social")
                    }
                }
                LazyRow(
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ), // Inner padding for LazyRow

                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color.White
                                )
                            }
                            Text("Your Story", fontSize = 12.sp)
                        }
                    }
                    items(stories.distinctBy { it.userId }) { story ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            StoryRing(
                                imageUrl = story.imageUrl,
                                onClick = {
                                    val userStories = stories.filter { it.userId == story.userId }
                                    showViewer = userStories
                                    currentStoryIndex = 0
                                }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
        Spacer(
            Modifier.height(20.dp)
        )
        val todayPlan = dietPlans.find { it.day == todayName }
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Your meal for today")
                    TextButton(onClick = {
                        navController.navigate(Screens.DietPlans.name)
                    }) {
                        Text("View Meal Plans")
                    }
                }

                if (todayPlan != null) {
                    todayPlan.meals.sortedBy { it.time_of_day }.forEach { meal ->
                        MinimalMealCard(meal)
                    }
                } else {
                    Text("No meal plan for today.")
                }
            }
        }
    }

    if (showViewer != null) {
        StoryViewer(
            stories = showViewer!!,
            startIndex = currentStoryIndex,
            onClose = { showViewer = null }
        )
    }
}

@Composable
fun MinimalMealCard(meal: Meal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            ,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(meal.time_of_day, style = MaterialTheme.typography.titleSmall)
            Text(meal.food_recommended, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
