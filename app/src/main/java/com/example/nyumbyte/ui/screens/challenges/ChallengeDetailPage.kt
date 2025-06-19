package com.example.nyumbyte.ui.screens.challenges

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nyumbyte.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.material3.MaterialTheme // ✅ CORRECT

data class Particle(val angle: Float, val speed: Float, val color: Color, val radius: Float)

@Composable
fun ChallengeDetailPage(
    navController: NavController,
    challengeId: String?,
    userId: String
) {
    var challenge by remember { mutableStateOf<Challenge?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showParticles by remember { mutableStateOf(false) }
    var showExpPopup by remember { mutableStateOf(false) }
    var showPointsPopup by remember { mutableStateOf(false) }

    val particles = remember { mutableStateListOf<Particle>() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(challengeId) {
        if (challengeId == null) return@LaunchedEffect
        try {
            val doc = Firebase.firestore.collection("challenges").document(challengeId).get().await()
            val loaded = doc.toObject(Challenge::class.java)
            val completedMap = doc.get("id") as? Map<String, Boolean> ?: emptyMap()
            if (loaded != null) {
                challenge = loaded.copy(
                    docId = doc.id,
                    completed = completedMap[userId] == true
                )
            }
        } catch (_: Exception) {
        } finally {
            isLoading = false
        }
    }

    if (isLoading || challenge == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val c = challenge!!
    val pointReward = when (c.category.lowercase()) {
        "easy" -> 10
        "medium" -> 20
        "hard" -> 30
        else -> 5
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(id = ChallengeRepository.getImageResource(c.imageName)),
                contentDescription = c.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = c.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = c.description,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!c.completed) {
                        Button(
                            onClick = {
                                scope.launch {
                                    val challengeRef = Firebase.firestore.collection("challenges").document(challengeId!!)
                                    val snapshot = challengeRef.get().await()
                                    val idMap = snapshot.get("id") as? Map<String, Boolean> ?: emptyMap()

                                    if (userId !in idMap.keys) {
                                        challengeRef.update("id.$userId", true).await()
                                        ChallengeRepository.addExp(userId, c.expReward, c.category)

                                        challenge = c.copy(completed = true)

                                        particles.clear()
                                        repeat(150) {
                                            val angle = Random.nextFloat() * 360f
                                            val speed = Random.nextFloat() * 800f + 400f
                                            val radius = Random.nextFloat() * 10f + 10f
                                            val color = listOf(
                                                Color(0xFFFFC107), // Amber
                                                Color(0xFF03A9F4), // Light Blue
                                                Color(0xFFE91E63), // Pink
                                                Color(0xFF4CAF50), // Green
                                                Color(0xFFFF5722), // Deep Orange
                                                Color(0xFF9C27B0), // Purple
                                                Color(0xFF00BCD4), // Cyan
                                                Color(0xFFFFEB3B), // Yellow
                                                Color(0xFFCDDC39), // Lime
                                                Color(0xFF8BC34A), // Light Green
                                                Color(0xFF3F51B5), // Indigo
                                                Color(0xFF795548), // Brown
                                                Color(0xFF607D8B), // Blue Grey
                                                Color(0xFFFF8A65), // Light Orange
                                                Color(0xFFB2FF59), // Neon Green
                                                Color(0xFF69F0AE), // Mint
                                                Color(0xFFFF5252), // Vivid Red
                                                Color(0xFF448AFF), // Vivid Blue
                                            ).random()




                                            particles.add(Particle(angle, speed, color, radius))
                                        }

                                        showParticles = true
                                        showExpPopup = true
                                        showPointsPopup = true

                                        delay(1800)
                                        showExpPopup = false
                                        showPointsPopup = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Finish Challenge (+${c.expReward} EXP, +$pointReward pts)")
                        }
                    } else {
                        Text(
                            "Challenge Completed ✅",
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        if (showParticles) {
            ParticleExplosion(
                particles = particles,
                durationMillis = 2000,
                onDone = {
                    showParticles = false
                    particles.clear()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showExpPopup) {
            RewardPopup(
                text = "+${c.expReward} EXP",
                color = MaterialTheme.colorScheme.primary,
                alignment = Alignment.CenterStart,
                offsetX = 40.dp
            )
        }

        if (showPointsPopup) {
            RewardPopup(
                text = "+$pointReward pts",
                color = MaterialTheme.colorScheme.tertiary,
                alignment = Alignment.CenterEnd,
                offsetX = (-40).dp
            )
        }
    }
}

@Composable
fun RewardPopup(
    text: String,
    color: Color,
    alignment: Alignment = Alignment.Center,
    offsetX: Dp = 0.dp
) {
    val transition = rememberInfiniteTransition()
    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = alignment
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .offset(x = offsetX, y = offsetY.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun ParticleExplosion(
    particles: List<Particle>,
    durationMillis: Int,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis)
        )
        delay(300)
        onDone()
    }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        for (particle in particles) {
            val angleRad = Math.toRadians(particle.angle.toDouble())
            val dx = cos(angleRad) * particle.speed * progress.value
            val dy = sin(angleRad) * particle.speed * progress.value
            val alpha = (1f - progress.value).coerceIn(0.3f, 1f)
            drawCircle(
                color = particle.color.copy(alpha = alpha),
                radius = particle.radius,
                center = Offset(center.x + dx.toFloat(), center.y + dy.toFloat())
            )
        }
    }
}
