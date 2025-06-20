package com.example.nyumbyte.ui.screens.foodscanner

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nyumbyte.data.model.Health
import com.example.nyumbyte.data.repository.HealthRepository
import com.example.nyumbyte.ui.navigation.Screens
import com.example.nyumbyte.util.getCurrentWeekId
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun ResultScreen(
    label: String,
    navController: NavController,
    viewModel: FoodScannerViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    var suggestion by remember { mutableStateOf("Analyzing your food...") }
    var calorieText by remember { mutableStateOf("Estimating...") }

    LaunchedEffect(label) {
        val result = fetchGeminiSuggestion(label)
        suggestion = result.suggestion
        calorieText = "${result.calorie} kcal"

        val calorieInt = result.calorie.toIntOrNull() ?: 0
        viewModel.setCalorie(calorieInt)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Scan Results",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Detected Food", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = label,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Calorie Estimate", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = calorieText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Nutritional Insights", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(suggestion, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        sendToCalorieGoal(viewModel.calorie.value)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Add to Calorie Goal", style = MaterialTheme.typography.labelLarge)
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Scan Again", style = MaterialTheme.typography.labelLarge)
            }

            Button(
                onClick = { navController.navigate(Screens.Home.name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Return Home", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

suspend fun sendToCalorieGoal(calorieAmount: Int) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val week = getCurrentWeekId()
    val today = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

    try {
        val existing = HealthRepository.getHealthData(uid, week)
        val updatedCalorieMap = existing?.calorieIntake?.toMutableMap() ?: mutableMapOf()
        updatedCalorieMap[today] = (updatedCalorieMap[today] ?: 0) + calorieAmount

        val newHealth = Health(
            week = week,
            calorieIntake = updatedCalorieMap,
            waterIntake = existing?.waterIntake ?: emptyMap()
        )

        HealthRepository.saveHealthData(uid, week, newHealth)
        Log.d("CalorieGoal", "Saved $calorieAmount kcal to $today")
    } catch (e: Exception) {
        Log.e("CalorieGoal", "Error saving to Firebase", e)
    }
}
