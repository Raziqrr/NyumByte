package com.example.nyumbyte.ui.screens.social.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.nyumbyte.ui.screens.social.Story


@Composable
fun StoryViewer(stories: List<Story>, startIndex: Int, onClose: () -> Unit) {
    var index by remember { mutableStateOf(startIndex) }
    val current = stories.getOrNull(index)

    current?.let {
        AlertDialog(
            onDismissRequest = onClose,
            confirmButton = {
                if (index < stories.lastIndex) {
                    Button(onClick = { index++ }) { Text("Next") }
                } else {
                    Button(onClick = onClose) { Text("Close") }
                }
            },
            dismissButton = {
                if (index > 0) {
                    Button(onClick = { index-- }) { Text("Previous") }
                }
            },
            title = { Text("${it.userName}'s Story") },
            text = {
                Column {
                    AsyncImage(
                        model = it.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clickable {
                                if (index < stories.lastIndex) index++
                                else onClose()
                            }
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(it.description)
                }
            }
        )
    }
}
