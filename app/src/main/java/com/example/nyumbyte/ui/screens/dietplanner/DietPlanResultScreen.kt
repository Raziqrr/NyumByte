package com.example.nyumbyte.ui.screens.dietplanner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nyumbyte.data.model.Meal


@Composable
fun DietPlanResultScreen(
    dietPlanViewModel: DietPlanViewModel, // Use only ViewModel here
    modifier: Modifier = Modifier
) {
    // Collect the StateFlow<DietPlanUiState> from ViewModel
    val uiState by dietPlanViewModel.uiState.collectAsState()

    // Extract dietPlans from the uiState
    val dietPlans = uiState.dietPlans

    println(dietPlans) // If you want to debug

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Diet Plan",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (dietPlans.isEmpty()) {
            Text("No diet plan available.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(dietPlans) { plan ->
                    Text(
                        text = plan.day,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    plan.meals.forEach { meal ->
                        MealCard(meal)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

