package com.example.nyumbyte.ui.screens.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    var itemToConfirm by remember { mutableStateOf<Pair<String, String>?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("REWARDS CENTER", color = MaterialTheme.colorScheme.onPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Hit 300 for bonus 10 coins!", color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Icon(painter = painterResource(id = R.drawable.reward_star), contentDescription = "Star", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(64.dp))
                    Text("Your Points: $coinBalance \uD83E\uDE99", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = (coinBalance / 500f).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("500", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(20.dp))
            Text("\uD83C\uDF96 Badges Shop", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)

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
            Text("\uD83D\uDED2 Merch", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)

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
                        if (type == "badge") rewardViewModel.purchaseBadge(name)
                        else rewardViewModel.purchaseMerch(name)
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
                containerColor = MaterialTheme.colorScheme.surface,
                textContentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun BadgeItem(name: String, cost: Int, imageRes: Int, coinBalance: Int, alreadyClaimed: Boolean, onBuyClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.width(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = imageRes), contentDescription = name, modifier = Modifier.size(56.dp))
            Text(name, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
            Button(
                onClick = onBuyClick,
                enabled = !alreadyClaimed && coinBalance >= cost,
                colors = ButtonDefaults.buttonColors(containerColor = if (alreadyClaimed) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(if (alreadyClaimed) "Claimed" else "$cost \uD83E\uDE99", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun MerchItem(name: String, cost: Int, imageRes: Int, coinBalance: Int, alreadyClaimed: Boolean, onBuyClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.width(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = imageRes), contentDescription = name, modifier = Modifier.size(56.dp))
            Text(name, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
            Button(
                onClick = onBuyClick,
                enabled = !alreadyClaimed && coinBalance >= cost,
                colors = ButtonDefaults.buttonColors(containerColor = if (alreadyClaimed) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.tertiary),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(if (alreadyClaimed) "Claimed" else "$cost \uD83E\uDE99", fontSize = 12.sp)
            }
        }
    }
}
