/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-05-15 17:37:56
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-20 04:13:50
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/dietplanner/CreateDietPlan.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.dietplanner

import PrimaryButton
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import coil.decode.Decoder
import coil.request.ImageRequest
import coil.ImageLoader
import com.example.nyumbyte.data.network.firebase.UserUiState
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.common.CustomTopAppBar
import com.example.nyumbyte.ui.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDietPlan(
    navController: NavController,
    dietPlanViewModel: DietPlanViewModel,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel
) {
    var chosenGoal by remember { mutableStateOf("") }
    var targetDuration by remember { mutableStateOf("") }
    var physicalActivity by remember { mutableStateOf("") }
    var chosenSleepPattern by remember { mutableStateOf("") }
    var cookingSkill by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var chosenEatingSchedule by remember { mutableStateOf("") }


    val dietPlans by dietPlanViewModel.structuredDietPlan.collectAsState()
    val uiState by dietPlanViewModel.uiState.collectAsState()
    val userState by userViewModel.userUiState.collectAsState()

    val user = (userState as? UserUiState.Success)?.user
    
    
    
    if (user == null) {
        // This is just a temporary fallback, shouldn't happen
        println(user)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("⚠️ Failed to load user. Please try again.")
        }
        return
    }
    

        Scaffold(
            topBar = {
                CustomTopAppBar(
                    title = "Create Diet Plan",
                    onBackClick = {navController.popBackStack()},
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))) {

                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    item {
                        DropdownSelector(
                            label = "Goal",
                            options = dietGoalOptions,
                            selectedOption = chosenGoal,
                            onOptionSelected = { chosenGoal = it }
                        )
                    }

                    item {
                        DropdownSelector(
                            label = "Target Duration",
                            options = targetDurationOptions,
                            selectedOption = targetDuration,
                            onOptionSelected = { targetDuration = it }
                        )
                    }

                    item {
                        DropdownSelector(
                            label = "Physical Activity",
                            options = physicalActivityOptions,
                            selectedOption = physicalActivity,
                            onOptionSelected = { physicalActivity = it }
                        )
                    }

                    item {
                        DropdownSelector(
                            label = "Sleep Pattern",
                            options = sleepPatternOptions,
                            selectedOption = chosenSleepPattern,
                            onOptionSelected = { chosenSleepPattern = it }
                        )
                    }

                    item {
                        DropdownSelector(
                            label = "Cooking Skill",
                            options = cookingSkillOptions,
                            selectedOption = cookingSkill,
                            onOptionSelected = { cookingSkill = it }
                        )
                    }

                    item {
                        DropdownSelector(
                            label = "Eating Schedule",
                            options = eatingScheduleOptions,
                            selectedOption = chosenEatingSchedule,
                            onOptionSelected = { chosenEatingSchedule = it }
                        )
                    }

                    item {
                        DropdownSelector(
                            label = "Budget Range",
                            options = budgetOptions,
                            selectedOption = budget,
                            onOptionSelected = { budget = it }
                        )
                    }

                    item {
                        PrimaryButton(
                            text = "Generate Diet Plan",
                            onClick = {
                                val constraints = DietConstraints(user = user).apply {
                                    goal = chosenGoal
                                    targetTime = targetDuration
                                    physicalIntensity = physicalActivity
                                    sleepPattern = chosenSleepPattern
                                    eatingPattern = chosenEatingSchedule
                                    cookingAbility = cookingSkill
                                    budgetConstraints = budget
                                }
                                dietPlanViewModel.generateDietPlan(constraints)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            icon = null,
                            enabled = !uiState.isLoading && chosenGoal.isNotEmpty()
                        )
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }

                if (uiState.isLoading) {
                    val imageLoader = ImageLoader.Builder(LocalContext.current).build()
                    val gifPainter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("file:///android_asset/broco_loading.gif")
                            .build(),
                        imageLoader = imageLoader
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = gifPainter,
                                contentDescription = "Broccoli Loading",
                                modifier = Modifier.size(150.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Broco is preparing your meal plan...", color = Color.White)
                        }
                    }
                }
            }

            LaunchedEffect(uiState.dietPlans, uiState.isLoading) {
                if (!uiState.isLoading && uiState.dietPlans.isNotEmpty()) {
                    navController.navigate(Screens.DietPlanResult.name) {
                        popUpTo(Screens.CreateDietPlan.name) { inclusive = true }
                    }
                }
            }
        }
    

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isValid: Boolean = false,
    error: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                label = { Text(label) },
                readOnly = true,
                leadingIcon = leadingIcon?.let {
                    { Icon(imageVector = it, contentDescription = null) }
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        if (isValid) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Valid",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                isError = error != null,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}


@Composable
fun NumberInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}




