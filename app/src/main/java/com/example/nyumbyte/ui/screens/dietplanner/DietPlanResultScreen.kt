package com.example.nyumbyte.ui.screens.dietplanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nyumbyte.data.model.DietPlan
import com.example.nyumbyte.data.model.Meal
import com.example.nyumbyte.data.model.User
import com.example.nyumbyte.data.network.firebase.UserUiState
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.beta.LiquidGlass
import com.example.nyumbyte.ui.beta.LiquidGlassConfig
import com.example.nyumbyte.ui.beta.LiquidGlassText
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietPlanResultScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    dietPlanViewModel: DietPlanViewModel, // Use only ViewModel here
    modifier: Modifier = Modifier
) {
    // Collect the StateFlow<DietPlanUiState> from ViewModel
    val uiState by dietPlanViewModel.uiState.collectAsState()

    // Extract dietPlans from the uiState
    val dietPlans = uiState.dietPlans
    val userState by userViewModel.userUiState.collectAsState()
    var cachedUser by remember { mutableStateOf<User?>(null) }

    val user = when (userState) {
        is UserUiState.Success -> {
            val currentUser = (userState as UserUiState.Success).user
            cachedUser = currentUser
            currentUser
        }
        else -> cachedUser // fallback to last known user
    }

    if (user == null) {
        println(user)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("⚠️ Failed to load user. Please try again.")
        }
        return
    }


    var isSaving by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generated Diet Plan") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(),
                navigationIcon = {
                    IconButton(onClick = {
                        dietPlanViewModel.resetState()
                        navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            if (dietPlans.isNotEmpty() && !uiState.isSaved) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            isSaving = true
                            val updatedUser = user.copy(dietPlan = dietPlans)
                            userViewModel.saveUser(updatedUser)
                            dietPlanViewModel.markPlanAsSaved()
                            isSaving = false

                            // Show Snackbar with ✔️ animation
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "✔️ Diet Plan Saved Successfully!",
                                    withDismissAction = true
                                )
                            }
                        },
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .height(20.dp)
                                    .padding(end = 8.dp)
                            )
                            Text("Saving...")
                        } else {
                            Text("Use This Diet Plan")
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(dietPlans) { plan ->
                    ExpandableDietPlanCard(plan)
                }
            }
        }
    }


}

@Composable
fun ExpandableDietPlanCard(plan: DietPlan) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "RotationAnimation"
    )

    // Calculate once (recomposed safely)
    val totalCalories = remember(plan) {
        plan.meals.sumOf { extractCalories(it.nutritional_value) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "${plan.day} - 🔥 $totalCalories kcal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier.graphicsLayer {
                    rotationZ = rotationAngle
                }
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                plan.meals.forEach { meal ->
                    MealCard(meal)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


fun extractCalories(nutritionalValue: String): Int {
    // Example: "300 kcal, 25g protein, 40g carbs, 5g fat"
    val regex = Regex("""(\d+)\s*kcal""")
    return regex.find(nutritionalValue)?.groupValues?.get(1)?.toIntOrNull() ?: 0
}

