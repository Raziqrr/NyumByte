package com.example.nyumbyte.ui.screens.challenges

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nyumbyte.data.model.Challenge
import com.example.nyumbyte.ui.theme.NyumByteTheme


@Composable
fun ChallengePage(
    onBack: () -> Unit,
    onChallengeClick: (String) -> Unit,
    onSocialClick: () -> Unit,
    viewModel: ChallengeViewModel = viewModel()
) {
    NyumByteTheme {
        val user = viewModel.user
        val avatarRes = viewModel.avatarRes
        val level = user?.level ?: 1
        val exp = user?.exp ?: 0
        val userName = user?.userName ?: "User"
        val challenges = viewModel.challenges
        val isLoading = viewModel.isLoading
        val showExpPopup = viewModel.showExpPopup
        val showPointsPopup = viewModel.showPointsPopup

        val expToNextLevel = ChallengeRepository.getExpToNextLevel(level)
        val progress = exp.toFloat() / expToNextLevel.coerceAtLeast(1)

        LaunchedEffect(Unit) {
            viewModel.loadInitialData()
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
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Level $level - ${ChallengeRepository.getLevelName(level)}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(id = avatarRes),
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = progress.coerceIn(0f, 1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .height(10.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(
                            "$exp / $expToNextLevel EXP",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onSocialClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Go to Social Page", color = MaterialTheme.colorScheme.onSecondary)
                    }

                    Text(
                        "Challenges",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground,
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
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }

                                items(filtered) { challenge ->
                                    ChallengeCard(
                                        challenge = challenge,
                                        onChallengeClick = { onChallengeClick(it) }
                                    )
                                }
                            }
                        }
                    }
                }

                if (showExpPopup) {
                    RewardPopup(
                        text = "+EXP!",
                        color = MaterialTheme.colorScheme.primary,
                        alignment = Alignment.TopStart,
                        offsetX = 20.dp
                    )
                }

                if (showPointsPopup) {
                    RewardPopup(
                        text = "+Points!",
                        color = MaterialTheme.colorScheme.tertiary,
                        alignment = Alignment.TopEnd,
                        offsetX = (-20).dp
                    )
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(challenge: Challenge, onChallengeClick: (String) -> Unit) {
    val isCompleted = challenge.completed

    val cardColor = if (isCompleted)
        MaterialTheme.colorScheme.surfaceVariant
    else
        MaterialTheme.colorScheme.surface

    val titleColor = if (isCompleted)
        MaterialTheme.colorScheme.onSurfaceVariant
    else
        MaterialTheme.colorScheme.onSurface

    val descColor = if (isCompleted)
        MaterialTheme.colorScheme.outline
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    val textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
    val imageAlpha = if (isCompleted) 0.3f else 1f

    val borderColor = if (isCompleted)
        MaterialTheme.colorScheme.outlineVariant
    else
        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = !isCompleted) {
                onChallengeClick(challenge.docId)
            }
            .border(BorderStroke(2.dp, borderColor), shape = RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(60.dp)) {
                Image(
                    painter = painterResource(id = challenge.challengeImageRes),
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
                            .background(
                                MaterialTheme.colorScheme.tertiary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "DONE",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onTertiary,
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
