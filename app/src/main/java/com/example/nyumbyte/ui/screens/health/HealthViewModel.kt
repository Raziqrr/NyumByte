package com.example.nyumbyte.ui.screens.health

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nyumbyte.data.model.Health
import com.example.nyumbyte.data.repository.HealthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

class HealthViewModel : ViewModel() {
    private val _aiAnalysis = mutableStateOf<String?>(null)
    val aiAnalysis: State<String?> = _aiAnalysis
    private val _allergies = MutableStateFlow<List<String>>(emptyList())
    val allergies: StateFlow<List<String>> = _allergies

    private val _weight = MutableStateFlow(0.0)
    val weight: StateFlow<Double> = _weight

    private val _height = MutableStateFlow(0.0)
    val height: StateFlow<Double> = _height

    private val _calorieIntake = MutableStateFlow<Map<String, Int>>(emptyMap())
    val calorieIntake: StateFlow<Map<String, Int>> = _calorieIntake

    private val _waterIntake = MutableStateFlow<Map<String, Int>>(emptyMap())
    val waterIntake: StateFlow<Map<String, Int>> = _waterIntake

    fun saveUserHealthInfo(uid: String) {
        viewModelScope.launch {
            HealthRepository.saveUserHealthInfo(
                uid = uid,
                weight = _weight.value,
                height = _height.value,
                allergies = _allergies.value
            )
        }
    }


    fun generateAIAnalysis(prompt: String) {
        viewModelScope.launch {
            val result = GeminiRepository.analyzeHealthPrompt(prompt)
            Log.d("GeminiAI", "AI Result: $result") // ADD THIS
            _aiAnalysis.value = result
        }
    }

    fun loadUserHealthInfo(uid: String) {
        viewModelScope.launch {
            val info = HealthRepository.getUserHealthInfo(uid)
            info?.let {
                _weight.value = it.first
                _height.value = it.second
                _allergies.value = it.third
            }
        }
    }

    fun loadHealthData(uid: String, week: String) {
        viewModelScope.launch {
            val health = HealthRepository.getHealthData(uid, week)
            health?.let {
                _calorieIntake.value = it.calorieIntake
                _waterIntake.value = it.waterIntake
            }
        }
    }

    fun saveHealthData(uid: String, week: String) {
        viewModelScope.launch {
            val health = Health(
                week = week,
                calorieIntake = _calorieIntake.value,
                waterIntake = _waterIntake.value
            )
            HealthRepository.saveHealthData(uid, week, health)
        }
    }

    fun updateCalorieForDay(day: String, value: Int) {
        _calorieIntake.value = _calorieIntake.value.toMutableMap().apply {
            this[day] = value
        }
    }

    fun updateWaterForDay(day: String, value: Int) {
        _waterIntake.value = _waterIntake.value.toMutableMap().apply {
            this[day] = value
        }
    }

    // ✅ Add these setters for editing
    fun updateWeight(newWeight: Double) {
        _weight.value = newWeight
    }

    fun updateHeight(newHeight: Double) {
        _height.value = newHeight
    }

    fun updateAllergies(newAllergies: List<String>) {
        _allergies.value = newAllergies
    }
}
