/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-10 01:07:56
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-10 01:08:31
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/dietplanner/MealCard.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.dietplanner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nyumbyte.data.model.Meal

@Composable
fun MealCard(meal: Meal) {
    var isChecked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(12.dp)
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                modifier = Modifier.padding(end = 12.dp)
            )

            Column {
                Text("🍽 ${meal.time_of_day}", fontWeight = FontWeight.Medium)
                Text("• Food: ${meal.food_recommended}")
                Text("• Detail: ${meal.food_detail}")
                Text("• Nutrition: ${meal.nutritional_value}")
                Text("• Prep: ${meal.preparation}")
            }
        }
    }
}