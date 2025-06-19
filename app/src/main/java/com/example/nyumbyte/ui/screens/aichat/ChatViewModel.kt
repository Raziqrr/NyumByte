package com.example.mobileproject.screens.ai_assisstant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileproject.ai.GeminiProvider
import com.example.nyumbyte.ui.screens.aichat.ChatRepository
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AIChatScreenUIState())
    val uiState = _uiState.asStateFlow()
    private val conversationHistory = mutableListOf<Content>()

    fun onInputChanged(newInput: String) {
        _uiState.value = _uiState.value.copy(currentInput = newInput)
    }

    init {
        // Load chat history from repository on initialization
        _uiState.value = _uiState.value.copy(
            chatHistory =   ChatRepository.getChatHistory()
        )
    }
    fun loadChat(){}

    fun sendMessage() {
        val input = _uiState.value.currentInput.trim()
        if (input.isBlank()) return

        val userMessage = ChatMessage(message = input, isFromUser = true)
        val newHistory = _uiState.value.chatHistory + userMessage
        ChatRepository.saveChat(newHistory)
        _uiState.value = _uiState.value.copy(
            chatHistory = _uiState.value.chatHistory + userMessage,
            currentInput = "",
            isTyping = true
        )
        fun clearChat() {
            ChatRepository.clearChat()
            conversationHistory.clear()
            _uiState.value = _uiState.value.copy(
                chatHistory = emptyList(),
                currentInput = ""
            )
        }


        viewModelScope.launch {
            try {
                conversationHistory.add(content(role = "user") { text(input) })
                val response = GeminiProvider.model.generateContent(*conversationHistory.toTypedArray())
                val reply = response.text ?: "Hmm, I didn't get that, mate!"
                conversationHistory.add(content(role = "model") { text(reply) })

                val botMessages = reply.split("\n\n").mapNotNull {
                    it.trim().takeIf { line -> line.isNotEmpty() }
                }.map { ChatMessage(message = it, isFromUser = false) }

                _uiState.value = _uiState.value.copy(
                    chatHistory = _uiState.value.chatHistory + botMessages,
                    isTyping = false
                )

                val fullHistory = newHistory + botMessages
                ChatRepository.saveChat(fullHistory)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    chatHistory = _uiState.value.chatHistory + ChatMessage(
                        message = "⚠️ Error: ${e.localizedMessage}",
                        isFromUser = false,
                        timestamp = System.currentTimeMillis()
                    ),
                    isTyping = false
                )
            }

        }
    }
}