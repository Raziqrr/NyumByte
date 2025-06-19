package com.example.nyumbyte.ui.screens.social.friend

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nyumbyte.ui.screens.challenges.ChallengeRepository.getLevelIcon
import com.example.nyumbyte.ui.screens.social.FriendProfile

@Composable
fun FriendCard(
    friendList: List<FriendProfile>,
    onRemoveRequest: (String) -> Unit
) {
    Column {
        friendList.forEach { friend ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Image(
                            painter = painterResource(getLevelIcon(friend.level)),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Name: ${friend.userName}", fontSize = 16.sp)
                            Text("Level: ${friend.level}", fontSize = 14.sp)
                            Text("ID: ${friend.id}", fontSize = 12.sp)
                        }
                    }
                    IconButton(onClick = { onRemoveRequest(friend.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
