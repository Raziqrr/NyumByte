/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-10 01:07:56
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-20 06:04:16
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/dietplanner/MealCard.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.dietplanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nyumbyte.data.model.Meal
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.example.nyumbyte.ui.beta.LiquidGlass
import com.example.nyumbyte.ui.beta.LiquidGlassConfig
import com.example.nyumbyte.ui.beta.LiquidGlassText
import com.example.nyumbyte.ui.theme.carbBorder
import com.example.nyumbyte.ui.theme.carbColor
import com.example.nyumbyte.ui.theme.defaultBorder
import com.example.nyumbyte.ui.theme.defaultColor
import com.example.nyumbyte.ui.theme.fatBorder
import com.example.nyumbyte.ui.theme.fatColor
import com.example.nyumbyte.ui.theme.kcalBorder
import com.example.nyumbyte.ui.theme.kcalColor
import com.example.nyumbyte.ui.theme.proteinBorder
import com.example.nyumbyte.ui.theme.proteinColor

@Composable
fun MealCard(meal: Meal) {
    var expanded by remember { mutableStateOf(false) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "expandIconRotation"
    )


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            ,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "🍽 ${meal.time_of_day}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(meal.food_recommended, style = MaterialTheme.typography.bodyLarge)
                }

                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationAngle)
                )
            }

            // Expandable animated content
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    Text(meal.food_detail, style = MaterialTheme.typography.bodyMedium)
                    
                    Spacer(Modifier.height(4.dp))
                    Text("Preparation", fontWeight = FontWeight.SemiBold)
                    Text(meal.preparation, style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(4.dp))
                    Text("Nutrition", fontWeight = FontWeight.SemiBold)

                    val nutritionChips = meal.nutritional_value.split(",").map { it.trim() }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        nutritionChips.forEach { chip ->
                            val (chipColor, borderColor) = when {
                                chip.contains("kcal", ignoreCase = true) -> kcalColor to kcalBorder
                                chip.contains("protein", ignoreCase = true) -> proteinColor to proteinBorder
                                chip.contains("carb", ignoreCase = true) -> carbColor to carbBorder
                                chip.contains("fat", ignoreCase = true) -> fatColor to fatBorder
                                else -> defaultColor to defaultBorder
                            }

                            Box(
                                modifier = Modifier
                                    .height(32.dp)
                                    .padding(0.dp)
                                    .shadow(4.dp, shape = MaterialTheme.shapes.small)
                                    .border(1.dp, borderColor, shape = MaterialTheme.shapes.small)
                            ) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(chip) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = chipColor,
                                        labelColor = Color.White
                                    ),
                                    modifier = Modifier.padding(0.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewMealCard() {
    val sampleMeal = Meal(
        time_of_day = "Lunch",
        food_recommended = "Grilled Chicken Wrap",
        food_detail = "Whole wheat tortilla with grilled chicken, lettuce, and hummus.",
        nutritional_value = "450 kcal, 35g protein, 30g carbs, 15g fat",
        preparation = "Grill chicken breast, slice it, and wrap it with lettuce and hummus in tortilla.",
        imagePath = ""
        // Uncomment below if you add `ingredients` to the model
        // ingredients = listOf("Chicken breast", "Lettuce", "Tortilla", "Hummus")
    )

    MaterialTheme {
        MealCard(meal = sampleMeal)
    }
}

