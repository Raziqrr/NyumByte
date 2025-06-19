package com.example.nyumbyte.ui.screens.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nyumbyte.R

@Composable
fun RewardsPage(
    onBack: () -> Unit,
    rewardViewModel: RewardViewModel = viewModel()
) {
    val coinBalance by rewardViewModel.coinBalance.collectAsState()
    val claimedBadges by rewardViewModel.claimedBadges.collectAsState()
    val claimedMerch by rewardViewModel.claimedMerch.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var itemToConfirm by remember { mutableStateOf<Pair<String, String>?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFAA00FF), Color(0xFF6200EA)))),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("REWARDS CENTER", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Hit 300 for bonus 10 coins!", color = Color.White, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Icon(painter = painterResource(id = R.drawable.reward_star), contentDescription = "Star", tint = Color.Yellow, modifier = Modifier.size(64.dp))
                    Text("Your Points: $coinBalance 🪙", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = (coinBalance / 500f).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50)),
                color = Color(0xFFFFD600),
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0", color = Color.White)
                Text("500", color = Color.White)
            }

            Spacer(Modifier.height(20.dp))
            Text("🎖 Badges Shop", style = MaterialTheme.typography.titleMedium, color = Color.White)

            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("No Cap", "Sip God", "Beast").forEach { badgeName ->
                    BadgeItem(
                        name = badgeName,
                        cost = 25,
                        imageRes = when (badgeName) {
                            "No Cap" -> R.drawable.nocap
                            "Sip God" -> R.drawable.sipgod
                            else -> R.drawable.challengebeast
                        },
                        coinBalance = coinBalance,
                        alreadyClaimed = claimedBadges.contains(badgeName),
                        onBuyClick = { itemToConfirm = "badge" to badgeName }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("🛍 Merch", style = MaterialTheme.typography.titleMedium, color = Color.White)

            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("Shirt" to 800, "Cap" to 700, "Tumbler" to 350).forEach { (name, cost) ->
                    MerchItem(
                        name = name,
                        cost = cost,
                        imageRes = when (name) {
                            "Shirt" -> R.drawable.shirt
                            "Cap" -> R.drawable.cap
                            else -> R.drawable.tumbler
                        },
                        coinBalance = coinBalance,
                        alreadyClaimed = claimedMerch.contains(name),
                        onBuyClick = { itemToConfirm = "merch" to name }
                    )
                }
            }
        }

        itemToConfirm?.let { (type, name) ->
            AlertDialog(
                onDismissRequest = { itemToConfirm = null },
                confirmButton = {
                    TextButton(onClick = {
                        if (type == "badge") {
                            rewardViewModel.purchaseBadge(name)
                        } else {
                            rewardViewModel.purchaseMerch(name)
                        }
                        itemToConfirm = null
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToConfirm = null }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Confirm Purchase") },
                text = { Text("Are you sure you want to buy \"$name\"?") },
                containerColor = Color.White,
                textContentColor = Color.Black
            )
        }
    }
}

@Composable
fun BadgeItem(name: String, cost: Int, imageRes: Int, coinBalance: Int, alreadyClaimed: Boolean, onBuyClick: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.width(100.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = imageRes), contentDescription = name, modifier = Modifier.size(56.dp))
            Text(name, color = Color.White, fontSize = 12.sp)
            Button(
                onClick = onBuyClick,
                enabled = !alreadyClaimed && coinBalance >= cost,
                colors = ButtonDefaults.buttonColors(containerColor = if (alreadyClaimed) Color.Gray else Color(0xFF00E676)),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(if (alreadyClaimed) "Claimed" else "$cost 🪙", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun MerchItem(name: String, cost: Int, imageRes: Int, coinBalance: Int, alreadyClaimed: Boolean, onBuyClick: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.width(100.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = imageRes), contentDescription = name, modifier = Modifier.size(56.dp))
            Text(name, color = Color.White, fontSize = 12.sp)
            Button(
                onClick = onBuyClick,
                enabled = !alreadyClaimed && coinBalance >= cost,
                colors = ButtonDefaults.buttonColors(containerColor = if (alreadyClaimed) Color.Gray else Color(0xFF00B0FF)),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(if (alreadyClaimed) "Claimed" else "$cost 🪙", fontSize = 12.sp)
            }
        }
    }
}
