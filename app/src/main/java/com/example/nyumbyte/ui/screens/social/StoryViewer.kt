package com.example.nyumbyte.ui.screens.social.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nyumbyte.ui.screens.social.Story

@Composable
fun StoryViewer(stories: List<Story>, startIndex: Int, onClose: () -> Unit) {
    var index by remember { mutableStateOf(startIndex) }
    val current = stories.getOrNull(index)

    current?.let {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable {
                    if (index < stories.lastIndex) index++
                    else onClose()
                }
        ) {
            AsyncImage(
                model = it.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header with user info and close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = it.userName,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                // Bottom description with navigation
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.Black.copy(alpha = 0.6f)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = it.description,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (index > 0) {
                            TextButton(onClick = { index-- }) {
                                Text("Previous", color = Color.White)
                            }
                        }
                        if (index < stories.lastIndex) {
                            TextButton(onClick = { index++ }) {
                                Text("Next", color = Color.White)
                            }
                        } else {
                            TextButton(onClick = onClose) {
                                Text("Close", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}