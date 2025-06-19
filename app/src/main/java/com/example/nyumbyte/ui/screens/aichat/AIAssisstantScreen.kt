/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-05-08 17:26:41
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-20 04:21:59
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/aichat/AIAssisstantScreen.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.mobileproject.screens.ai_assisstant

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.ai.client.generativeai.Chat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavController
import com.example.nyumbyte.R
import com.example.nyumbyte.ui.common.CustomTopAppBar
import dev.jeziellago.compose.markdowntext.MarkdownText


@Composable
fun AIAssisstantScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.chatHistory.size) {

        if (uiState.chatHistory.isNotEmpty()) {

            val lastMessageIndex = uiState.chatHistory.size - 1

            val lastMessage = uiState.chatHistory.last()


            val targetIndex = if (!lastMessage.isFromUser) {

                // Last message is from AI, find the start of this AI's continuous response block

                var firstIndexOfCurrentAiResponse = lastMessageIndex

                // Iterate backwards from the message *before* the last one

                for (i in (lastMessageIndex - 1) downTo 0) {

                    if (uiState.chatHistory[i].isFromUser) {

                        // The message at index 'i' is from the user.

                        // So, the current AI response block started at 'i + 1'.

                        firstIndexOfCurrentAiResponse = i + 1

                        break // Found the boundary

                    } else {

                        // The message at index 'i' is also from the AI.

                        // This means it's an earlier part of the same continuous AI response.

                        // So, update 'firstIndexOfCurrentAiResponse' to this earlier index 'i'.

                        firstIndexOfCurrentAiResponse = i

                    }

                }

                // If the loop completes without finding a user message (e.g., all initial messages are AI),

                // 'firstIndexOfCurrentAiResponse' will correctly be 0 or the index of the earliest AI message in the block.

                firstIndexOfCurrentAiResponse

            } else {

                // Last message is from User, scroll to this message

                lastMessageIndex

            }

            // Animate scroll to the determined target index

            listState.animateScrollToItem(index = targetIndex)

        }

    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 10.dp)
    ) {
        // Chat History
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.Bottom)

        ) {
            items(uiState.chatHistory) { chatMessage ->
                ChatMessageBubble(chatMessage = chatMessage)
            }
            item {
                if (uiState.isTyping) {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        ProfileImageBubble(R.drawable.broco, false)
                        TypingIndicatorBubble(
                            isVisible = uiState.isTyping
                        )
                    }
                }
            }
        }

        // User Input
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Gray.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small)
                    .padding(16.dp)
            ) {
                if (uiState.currentInput.isEmpty()) {
                    Text(
                        text = "Say something to Broco...",
                        color = Color.Gray
                    )
                }
                BasicTextField(
                    value = uiState.currentInput,
                    onValueChange = {
                        viewModel.onInputChanged(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            viewModel.sendMessage()
                            keyboardController?.hide()
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = {
                viewModel.sendMessage()
                keyboardController?.hide()
            }) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

    }
    CustomTopAppBar(
        title = "Chat with Broco",
        onBackClick = {navController.popBackStack()},
    )
}

@Composable
fun ChatMessageBubble(chatMessage: ChatMessage) {
    val isUser = chatMessage.isFromUser

    val bubbleColor =
        if (isUser) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
    val bubbleShape = if (isUser) RoundedCornerShape(20.dp, 0.dp, 20.dp, 20.dp)
    else RoundedCornerShape(0.dp, 20.dp, 20.dp, 20.dp)

    val bubbleAlignment = if (isUser) Alignment.End else Alignment.Start
    val profilePadding = 6.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (chatMessage.isFromUser) 40.dp else 0.dp,
                end = if (chatMessage.isFromUser) 0.dp else 40.dp,
            ),
        horizontalArrangement = if (chatMessage.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            ProfileImageBubble(R.drawable.broco, false)
            Spacer(modifier = Modifier.width(profilePadding))
        }
        Column(
            modifier = Modifier
                .padding(top = 10.dp)
        ) {
            Card(
                shape = bubbleShape,
                colors = CardDefaults.cardColors(containerColor = bubbleColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .widthIn(max = 300.dp) // Limit bubble width for readability
            ) {
                Column {
                    MarkdownText(
                        markdown = chatMessage.message,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(profilePadding))
            ProfileImageBubble(R.drawable.raziq, true)
        }
    }
}

@Composable
fun ProfileImageBubble(
    @DrawableRes id: Int,
    user: Boolean
) {
    val backgroundColor =
        if (user) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer

    Image(
        painter = painterResource(id),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = backgroundColor,
                shape = CircleShape // ✅ specify the shape!
            )
    )
}

@Composable
fun TypingIndicatorBubble(isVisible: Boolean) {
    val transition = updateTransition(targetState = isVisible, label = "Typing Bubble Transition")

    val borderColor by transition.animateColor(label = "Border Color") { visible ->
        if (visible) Color.Magenta else Color.Transparent
    }

    val elevation by transition.animateDp(label = "Elevation") { visible ->
        if (visible) 8.dp else 0.dp
    }

    val infiniteTransition = rememberInfiniteTransition(label = "Typing Dots")

    // Dot bouncing animation (wave style)
    val dot1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            tween(400, easing = LinearEasing, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ), label = "Dot 1"
    )

    val dot2Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            tween(280, easing = LinearEasing, delayMillis = 100),
            repeatMode = RepeatMode.Reverse
        ), label = "Dot 2"
    )

    val dot3Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            tween(190, easing = LinearEasing, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ), label = "Dot 3"
    )

    // Only show if visible
    if (isVisible) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .animateContentSize()
                .background(Color.LightGray, RoundedCornerShape(16.dp))
                .padding(horizontal = 17.dp, vertical = 12.dp)
                .clip(
                    shape = RoundedCornerShape(5.dp)
                )
                .shadow(
                    elevation = 10.dp
                )
        ) {
            listOf(dot1Offset, dot2Offset, dot3Offset).forEachIndexed { index, offset ->

                Box(
                    modifier = Modifier
                        .offset(y = offset.dp)
                        .size(6.dp)
                        .padding(horizontal = 2.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clip(
                            shape = RoundedCornerShape(5.dp)
                        )
                        .shadow(elevation = 10.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalCoilApi::class)
@Composable
fun LoadingAnimation(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Displaying a loading GIF
        Image(
            painter = rememberAsyncImagePainter(R.drawable.broco_loading), // path to the GIF
            contentDescription = "Loading",
            modifier = Modifier.size(50.dp) // Adjust size of the GIF
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingAnimation() {
    LoadingAnimation()
}