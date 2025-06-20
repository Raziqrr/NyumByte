package com.example.nyumbyte.ui.screens.health

import PrimaryButton
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import android.graphics.Color
import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nyumbyte.ui.common.CustomTopAppBar
import com.example.nyumbyte.ui.common.SecondaryButton
import com.example.nyumbyte.util.getCurrentWeekId
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun HealthAnalyticsScreen(
    uid: String,
    viewModel: HealthViewModel = viewModel(),
    navController: NavController
) {
    val aiAnalysis by viewModel.aiAnalysis

    val calorieMap by viewModel.calorieIntake.collectAsState()
    val waterMap by viewModel.waterIntake.collectAsState()
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val week: String = remember { getCurrentWeekId() }

    val weight by viewModel.weight.collectAsState()
    val height by viewModel.height.collectAsState()
    val allergies by viewModel.allergies.collectAsState()


    var showDialog by remember { mutableStateOf(false) }
    var showInputDialog by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf("Monday") }
    var calorieInput by remember { mutableStateOf("") }
    var waterInput by remember { mutableStateOf("") }
    val prompt = formatHealthPrompt(calorieMap, waterMap)
    LaunchedEffect(prompt) {
        Log.d("AI_PROMPT", prompt)
    }

    LaunchedEffect(uid) {
        viewModel.loadHealthData(uid, week)
        viewModel.loadUserHealthInfo(uid) // <- Add this
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // ✅ This makes everything scrollable
    ) {
        Spacer(Modifier.height(100.dp))
        Text("Health Analytics", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        DisplayHealthInfo(weight, height, allergies)
        Spacer(Modifier.height(16.dp))

        Button(onClick = { showDialog = true }) {
            Text("Edit Health Info")
        }
        Spacer(Modifier.height(16.dp))
        Text("Weekly Overview", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        WeeklyIntakeChart(
            calorieMap = mapToShortDays(calorieMap),
            waterMap = mapToShortDays(waterMap)
        )
        Spacer(Modifier.height(16.dp))

        Spacer(Modifier.height(16.dp))
        aiAnalysis?.let {
            Text("AI Analysis:", style = MaterialTheme.typography.titleMedium)
            Text(it)
        }
        Spacer(Modifier.height(16.dp))

        daysOfWeek.forEach { day ->
            val calories = calorieMap[day] ?: 0
            val water = waterMap[day] ?: 0

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(day, style = MaterialTheme.typography.titleMedium)
                    Text("Calories: $calories kcal")
                    Text("Water: $water ml")
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(30.dp))
        // ✅ Now this will be visible
        SecondaryButton(
            onClick = { showInputDialog = true },
            text = "+ Add Daily Intake",
        )

        Spacer(Modifier.height(20.dp))

        PrimaryButton(
            onClick = {
                viewModel.saveHealthData(uid, week)
            },
            text = "Save Health Data",
        )

        Spacer(Modifier.height(30.dp))
    }

    if (showDialog) {
        EditHealthInfo(
            weight = weight,
            height = height,
            allergies = allergies,
            onWeightChange = { viewModel.updateWeight(it) },     // ✅ update via ViewModel
            onHeightChange = { viewModel.updateHeight(it) },
            onAllergyChange = { viewModel.updateAllergies(it) },
            onDismiss = { showDialog = false
                viewModel.saveUserHealthInfo(uid) // ✅ SAVE TO FIRESTORE ON CLOSE
            }
        )
    }

    if (showInputDialog) {
        AlertDialog(
            onDismissRequest = { showInputDialog = false },
            title = { Text("Add Intake") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DropdownMenuBox(
                        label = "Select Day",
                        options = daysOfWeek,
                        selectedOption = selectedDay,
                        onOptionSelected = { selectedDay = it }
                    )
                    OutlinedTextField(
                        value = calorieInput,
                        onValueChange = { calorieInput = it },
                        label = { Text("Calories (kcal)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = waterInput,
                        onValueChange = { waterInput = it },
                        label = { Text("Water (ml)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val calories = calorieInput.toIntOrNull() ?: 0
                    val water = waterInput.toIntOrNull() ?: 0
                    viewModel.updateCalorieForDay(selectedDay, calories)
                    viewModel.updateWaterForDay(selectedDay, water)
                    viewModel.saveHealthData(uid, week)
                    showInputDialog = false
                    calorieInput = ""
                    waterInput = ""
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInputDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    CustomTopAppBar(
        title = "My Diet Plan",
        onBackClick = { navController.popBackStack() }
    )
}

@Composable
fun WeeklyIntakeChart(
    calorieMap: Map<String, Int>,
    waterMap: Map<String, Int>
) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                legend.isEnabled = true
                axisRight.isEnabled = false
                axisLeft.axisMinimum = 0f
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.granularity = 1f
            }
        },
        update = { chart ->
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

            val calorieEntries = days.mapIndexed { index, day ->
                BarEntry(index.toFloat(), (calorieMap[day] ?: 0).toFloat())
            }

            val waterEntries = days.mapIndexed { index, day ->
                BarEntry(index.toFloat() + 0.3f, (waterMap[day] ?: 0).toFloat())
            }

            val calorieDataSet = BarDataSet(calorieEntries, "Calories").apply {
                color = Color.rgb(255, 99, 132)
            }

            val waterDataSet = BarDataSet(waterEntries, "Water (ml)").apply {
                color = Color.rgb(54, 162, 235)
            }

            val data = BarData(calorieDataSet, waterDataSet).apply {
                barWidth = 0.25f
            }

            chart.data = data
            chart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(days)
                labelCount = days.size
            }

            chart.groupBars(0f, 0.4f, 0.05f) // group bars
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}
@Composable
fun DisplayHealthInfo(
    weight: Double,
    height: Double,
    allergies: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Weight: ${weight} kg", style = MaterialTheme.typography.bodyLarge)
        Text("Height: ${height} cm", style = MaterialTheme.typography.bodyLarge)
        Text("Allergies: ${if (allergies.isNotEmpty()) allergies.joinToString(", ") else "None"}", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun EditHealthInfo(
    weight: Double,
    height: Double,
    allergies: List<String>,
    onWeightChange: (Double) -> Unit,
    onHeightChange: (Double) -> Unit,
    onAllergyChange: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var weightText by remember { mutableStateOf(weight.toString()) }
    var heightText by remember { mutableStateOf(height.toString()) }
    var allergiesText by remember { mutableStateOf(allergies.joinToString(", ")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Health Info") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = it },
                    label = { Text("Height (cm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = allergiesText,
                    onValueChange = { allergiesText = it },
                    label = { Text("Allergies (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onWeightChange(weightText.toDoubleOrNull() ?: 0.0)
                onHeightChange(heightText.toDoubleOrNull() ?: 0.0)
                onAllergyChange(allergiesText.split(",").map { it.trim() })
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DropdownMenuBox(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Day")
                }
            }
        )
        DropdownMenu(
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
}
fun mapToShortDays(data: Map<String, Int>): Map<String, Int> {
    val dayShortMap = mapOf(
        "Monday" to "Mon",
        "Tuesday" to "Tue",
        "Wednesday" to "Wed",
        "Thursday" to "Thu",
        "Friday" to "Fri",
        "Saturday" to "Sat",
        "Sunday" to "Sun"
    )
    return data.mapKeys { (key, _) -> dayShortMap[key] ?: key }
}

fun formatHealthPrompt(
    calorieMap: Map<String, Int>,
    waterMap: Map<String, Int>,
    calorieGoal: Int = 2000,
    waterGoal: Int = 2000
): String {
    val sb = StringBuilder()
    sb.append("Here is my health data for the week:\n\nCalories:\n")
    calorieMap.forEach { (day, value) -> sb.append("- $day: $value\n") }

    sb.append("\nWater (ml):\n")
    waterMap.forEach { (day, value) -> sb.append("- $day: $value\n") }

    sb.append("\nMy daily goal is $calorieGoal kcal and $waterGoal ml.")
    sb.append("\nWhich days did I meet both goals? Any suggestions or patterns?")

    return sb.toString()
}

