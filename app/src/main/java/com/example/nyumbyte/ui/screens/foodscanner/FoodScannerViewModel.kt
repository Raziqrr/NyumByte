package com.example.nyumbyte.ui.screens.foodscanner

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoodScannerViewModel : ViewModel() {

    private val _calorie = MutableStateFlow(0)
    val calorie: StateFlow<Int> = _calorie

    fun setCalorie(kcal: Int) {
        _calorie.value = kcal
    }
}
