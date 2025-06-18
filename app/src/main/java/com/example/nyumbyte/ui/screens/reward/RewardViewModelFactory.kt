package com.example.nyumbyte.ui.screens.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RewardViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RewardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RewardViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
