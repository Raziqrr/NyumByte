package com.example.nyumbyte.ui.screens.challenges

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nyumbyte.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

data class UserData(
    val level: Int = 1,
    val exp: Int = 0,
    val avatarRes: Int = R.drawable.default_avatar
)

@Composable
fun ChallengePage(
    onBack: () -> Unit,
    onChallengeClick: (String) -> Unit,
    onSocialClick: () -> Unit,
) {
    val currentUser = Firebase.auth.currentUser
    val userId = currentUser?.uid ?: return

    var userData by remember { mutableStateOf(UserData()) }
    var isLoading by remember { mutableStateOf(true) }
    var challenges by remember { mutableStateOf<List<Challenge>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        challenges = loadChallengesForUser(userId)

        Firebase.firestore.collection("Users").document(userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val level = snapshot.getLong("level")?.toInt() ?: 1
                    val exp = snapshot.getLong("exp")?.toInt() ?: 0
                    userData = UserData(
                        level = level,
                        exp = exp,
                        avatarRes = ChallengeRepository.getLevelIcon(level)
                    )
                    isLoading = false
                }
            }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val expToNextLevel = ChallengeRepository.getExpToNextLevel(userData.level)
    val progress = userData.exp.toFloat() / expToNextLevel

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
            )
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xAA1C1C1C))
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Level ${userData.level} - ${ChallengeRepository.getLevelName(userData.level)}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = userData.avatarRes),
                    contentDescription = "User Avatar",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    color = Color(0xFF00E676),
                    trackColor = Color(0xFF555555)
                )
                Text(
                    "${userData.exp} / $expToNextLevel EXP",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSocialClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5))
            ) {
                Text("Go to Social Page", color = Color.White)
            }

            Text(
                "Challenges",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Daily", "Easy", "Medium", "Hard").forEach { category ->
                    val filtered = challenges.filter { it.category == category }
                    if (filtered.isNotEmpty()) {
                        item {
                            Text(
                                "$category Challenges",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.Cyan,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(filtered) { challenge ->
                            ChallengeCard(challenge) { id ->
                                onChallengeClick(id)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(challenge: Challenge, onChallengeClick: (String) -> Unit) {
    val isCompleted = challenge.completed

    val cardColor = if (isCompleted) Color(0xFF2E2E2E) else Color(0xFF1C1C1C)
    val titleColor = if (isCompleted) Color(0xFFB0BEC5) else Color.White
    val descColor = if (isCompleted) Color(0xFF9E9E9E) else Color.LightGray
    val textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
    val imageAlpha = if (isCompleted) 0.25f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = !isCompleted) {
                onChallengeClick(challenge.docId)
            },
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(60.dp)) {
                Image(
                    painter = painterResource(id = challenge.imageRes),
                    contentDescription = challenge.title,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .alpha(imageAlpha),
                    contentScale = ContentScale.Crop
                )

                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(Color.Green, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "DONE",
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    challenge.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = titleColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (isCompleted) 14.sp else 16.sp,
                        textDecoration = textDecoration
                    )
                )
                Text(
                    challenge.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = descColor,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

