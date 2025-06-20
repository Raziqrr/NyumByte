package com.example.nyumbyte.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // <-- ✅ REQUIRED for `by` to work
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nyumbyte.R
import com.example.nyumbyte.data.model.Achievement
import com.example.nyumbyte.data.model.Challenge
import com.example.nyumbyte.data.model.DailyChallenge
import com.example.nyumbyte.data.model.NavBarItem
import com.example.nyumbyte.ui.navigation.Screens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    navController: NavHostController,
    uid: String,
    viewModel: ProfileViewModel = viewModel()
) {

    val dailyChallenges by viewModel.dailyChallenges.collectAsState()
    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    LaunchedEffect(uid) {
        viewModel.loadDailyChallenges(today,uid)
    }


    val xp by viewModel.userXp.collectAsState()
    val level by viewModel.userLevel.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    var carrotsEaten by remember { mutableStateOf(0f) }
    val streakDays by viewModel.streakCount.collectAsState()
    var challengeMarkedComplete by remember { mutableStateOf(false) } // NEW

    val scrollState = rememberScrollState()
    // Let's say each carrot eaten = 14 points (5 carrots max = 70 points)



    LaunchedEffect(uid) {
        viewModel.loadUserData(uid)
        viewModel.loadAchievements(uid)
        viewModel.loadDailyChallenges(today, uid)
        viewModel.loadStreakData(uid) // Load streak info from Firestore
    }



    val achievements by viewModel.achievements.collectAsState()
    val dummyItems = listOf(
        NavBarItem(Screens.Home.name, Icons.Default.Home, "Home"),
        NavBarItem(Screens.Broco.name, Icons.Default.ChatBubble, "Broco"),
        NavBarItem("Scan", Icons.Default.CameraAlt, "Scan"),
        NavBarItem("Rewards", icon = Icons.Default.Flag, "Rewards"),
        NavBarItem("profile", Icons.Default.Person, "Profile"),
        NavBarItem(Screens.ChallengePage.name, Icons.Default.Flag, "Challenges")
    )
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = true
    val bottomPadding = if (showBottomBar) 80.dp else 0.dp

    val name by viewModel.userName.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()



    when {
        loading -> CircularProgressIndicator()
        error != null -> Text("Error: $error")
        else -> Text("Welcome, $name!")
    }

    Scaffold(

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {

            // Animated Level Up message
            AnimatedVisibility(
                visible = carrotsEaten >= 5f,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -40 }),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🎉 You have leveled up! 🎉",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }

            //Text(text = "Level $level")
            //Text(text = "xp $xp")


            // Pass userName here!
            ProfileCard(
                level = level,
                xp = xp,
                userName = name
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Edit Profile Button opens dialog
            EditProfileButton(onEditClick = {
                showEditDialog = true
            })
            //Button(onClick = { viewModel.testAddAchievement(uid) }) {
              //  Text("Add Test Achievement")
            //}
            Spacer(modifier = Modifier.height(16.dp))
            AchievementTimeline(achievements = achievements)
            Spacer(modifier = Modifier.height(16.dp))
            DailyStreakTracker(streakDays = streakDays)
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(16.dp))
            BadgesSection()
            Spacer(modifier = Modifier.height(16.dp))
            ChallengesSection(
                challenges = dailyChallenges,
                onComplete = { challenge ->
                    viewModel.completeDailyChallenge(uid, challenge, today)
                },
                carrotsEaten = carrotsEaten,
                onCarrotsEatenChange = { carrotsEaten = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            RewardsSection(carrotsEaten = carrotsEaten)
        }
        if (showEditDialog) {
            EditNameDialog(
                currentName = name,
                onDismiss = { showEditDialog = false },
                onNameChange = { newName ->
                    viewModel.updateUserName(uid, newName)  // ✅ Sends update to Firestore
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun ProfileCard(level: Int, xp: Int,userName: String) {
    // Points relative to current level:
    // Same list used in ViewModel — keep it in sync
    val levelThresholds = listOf(0, 100, 250, 500, 800, 1200)

    val currentLevelIndex = level - 1
    val currentThreshold = levelThresholds.getOrNull(currentLevelIndex) ?: 0
    val nextThreshold = levelThresholds.getOrNull(currentLevelIndex + 1)

    val xpInCurrentLevel = xp - currentThreshold
    val xpToNextLevel = (nextThreshold ?: (currentThreshold + 100)) - xp
    val levelXpSpan = (nextThreshold ?: (currentThreshold + 100)) - currentThreshold

    val progress = xpInCurrentLevel.toFloat() / levelXpSpan.toFloat()


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.avocadolvl1),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.LightGray, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = userName,  // use the variable here!
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )

                    Text(
                        text = "Level $level",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Experience Progress",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(16.dp),
                color = if (level == 1) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,  // green for level 1
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )





            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (xpToNextLevel > 0) "$xpToNextLevel XP to next level" else "🎉 Maxed out!",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (xpToNextLevel > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary
            )

        }
    }
}



@Composable
fun AchievementTimeline(
    achievements: List<Achievement>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        achievements.forEachIndexed { index, achievement ->
            AchievementItem(
                achievement = achievement,
                isLast = index == achievements.lastIndex
            )
        }
    }
}
@Composable
fun AchievementItem(
    achievement: Achievement,
    isLast: Boolean
) {
    val primaryColor = MaterialTheme.colorScheme.primary  // Read it once here

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(40.dp)
                .padding(top = 8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = primaryColor,
                modifier = Modifier.size(16.dp)
            ) {}

            if (!isLast) {
                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier
                    .width(2.dp)
                    .height(48.dp)) {
                    drawLine(
                        color = primaryColor,  // Use the variable here
                        start = Offset(x = size.width / 2, y = 0f),
                        end = Offset(x = size.width / 2, y = size.height)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = achievement.title,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = achievement.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = achievement.date,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun EditProfileButton(onEditClick: () -> Unit) {
    Button(
        onClick = onEditClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Edit Profile")
    }
}

@Composable
fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Name") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onNameChange(text.trim())
                    } else {
                        // Optionally: ignore empty or show error (not implemented here)
                        onDismiss()
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun DailyStreakTracker(streakDays: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "🔥 Daily Streak",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "$streakDays day${if (streakDays > 1) "s" else ""} in a row",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (streakDays >= 7) {
                Text(
                    text = "🏅 Weekly Bonus!",
                    color = Color(0xFFE91E63),
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "Keep it up!",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ChallengesSection(
    challenges: List<DailyChallenge>,
    onComplete: (DailyChallenge) -> Unit,
    carrotsEaten: Float,
    onCarrotsEatenChange: (Float) -> Unit
) {
    Text(
        text = "Challenges",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (challenges.isEmpty()) {
            Text("No challenges loaded.")
        } else {
            challenges.forEach { challenge ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = challenge.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = challenge.description)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Reward: ${challenge.expReward} XP")
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!challenge.isCompleted) {
                            Button(
                                onClick = { onComplete(challenge) },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Mark as Done")
                            }
                        } else {
                            Text(
                                "✅ Completed",
                                color = Color.Green,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // Optional static challenge — keep for testing or remove
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Eat 5 carrots (Static Challenge)")
                Slider(
                    value = carrotsEaten,
                    onValueChange = onCarrotsEatenChange,
                    valueRange = 0f..5f,
                    steps = 4
                )
            }
        }
    }
}



@Composable
fun RewardsSection(carrotsEaten: Float) {
    Text(
        text = "Rewards",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    val earned20Points = carrotsEaten >= 5f
    val earned50Points = carrotsEaten >= 5f // or use another condition later

    if (earned20Points) {
        RewardCard(points = 20, label = "Leveled Up")
    }

    if (earned50Points) {
        RewardCard(points = 50, label = "Bonus Reward!")
    }

    if (!earned20Points && !earned50Points) {
        RewardCard(points = 0, label = "Keep Going!", isEmpty = true)
    }
}

@Composable
fun RewardCard(points: Int, label: String, isEmpty: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = if (isEmpty) "No rewards yet" else "Earned $points points")
            Text(
                text = label,
                color = if (isEmpty) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
fun BadgesSection() {
    Text(
        text = "Badges",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgeItem(R.drawable.badge2, "Sip God")
            BadgeItem(R.drawable.badge1, "Challenge Beast")
            BadgeItem(R.drawable.badge3, "No Cap")
        }
    }
}

@Composable
fun BadgeItem(imageRes: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            modifier = Modifier
                .size(100.dp) // Increased size for better visibility
                .clip(CircleShape) // Crops image into circle
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape), // Thicker border
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}