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

    // Load challenge
    LaunchedEffect(challengeId) {
        if (challengeId == null) return@LaunchedEffect
        try {
            val doc = Firebase.firestore.collection("challenges").document(challengeId).get().await()
            val loaded = doc.toObject(Challenge::class.java)
            if (loaded != null) {
                challenge = loaded.copy(id = doc.id)
            }
        } catch (_: Exception) {
        } finally {
            isLoading = false
        }
    }

    if (isLoading || challenge == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val c = challenge!!
    val pointReward = when (c.category.lowercase()) {
        "easy" -> 5
        "medium" -> 10
        "hard" -> 15
        else -> 3
    }

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
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
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
                colors = CardDefaults.cardColors(containerColor = Color(0xAA1C1C1C))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = c.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = c.description,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.LightGray),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!c.completed) {
                        Button(
                            onClick = {
                                scope.launch {
                                    // Add EXP and points
                                    ChallengeRepository.addExp(userId, c.expReward, c.category)

                                    // Mark challenge completed
                                    Firebase.firestore.collection("challenges")
                                        .document(c.id)
                                        .update("completed", true)
                                    challenge = c.copy(completed = true)

                                    // Trigger visuals
                                    particles.clear()
                                    repeat(150) {
                                        val angle = Random.nextFloat() * 360f
                                        val speed = Random.nextFloat() * 800f + 400f
                                        val radius = Random.nextFloat() * 10f + 10f
                                        val color = listOf(
                                            Color(0xFFFFC107), Color(0xFF03A9F4),
                                            Color(0xFFE91E63), Color(0xFF4CAF50), Color(0xFFFF5722)
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
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Finish Challenge (+${c.expReward} EXP, +$pointReward pts)")
                        }
                    } else {
                        Text(
                            "Challenge Completed ✅",
                            color = Color(0xFFB0BEC5),
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
                color = Color(0xFF00E676),
                alignment = Alignment.CenterStart,
                offsetX = 40.dp
            )
        }

        if (showPointsPopup) {
            RewardPopup(
                text = "+$pointReward pts",
                color = Color(0xFFFFC107),
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
                .background(Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp))
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
