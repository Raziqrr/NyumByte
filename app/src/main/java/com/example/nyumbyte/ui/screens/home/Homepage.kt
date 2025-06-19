/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-08 01:41:59
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-20 06:55:47
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/home/Homepage.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.nyumbyte.R
import com.example.nyumbyte.data.model.Meal

import com.example.nyumbyte.data.network.firebase.UserUiState
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.beta.LiquidGlass
import com.example.nyumbyte.ui.beta.LiquidGlassConfig
import com.example.nyumbyte.ui.beta.LiquidGlassDemo
import com.example.nyumbyte.ui.beta.LiquidGlassText
import com.example.nyumbyte.ui.navigation.Screens
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Homepage(
    userViewModel: UserViewModel,
    navController: NavHostController
){
    val userState by userViewModel.userUiState.collectAsState()
    val user = (userState as? UserUiState.Success)?.user
    val dietPlans = user?.dietPlan ?: emptyList()

    val todayName = remember {
        LocalDate.now().dayOfWeek.name.lowercase()
            .replaceFirstChar { it.uppercase() }
    } // e.g., "Thursday"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Card { 
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Today's Calorie Count"
                )

                if (user != null) {
                    Text(
                        "${user.calorieToday}"
                    )
                }
            }
        }
        Spacer(
            Modifier.height(10.dp)
        )
        
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Friends Activity")
                    Text("Go to social bubble")
                }
                Row(
                ) {
                    Text("Friend 1")
                    Text("Friend 2")
                }
            }
        }
        Spacer(
            Modifier.height(10.dp)
        )
        
        val todayPlan = dietPlans.find { it.day == todayName }
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your meal for today")
                TextButton(onClick = {
                    navController.navigate(Screens.DietPlans.name)
                }) {
                    Text("View Meal Plans")
                }
            }

            if (todayPlan != null) {
                todayPlan.meals.sortedBy { it.time_of_day }.forEach { meal ->
                    MinimalMealCard(meal)
                }
            } else {
                Text("No meal plan for today.")
            }
        }
        Card { 
            Column { 
                Text("Your rewards")
                Card {  }
            }
        }
    }
}

@Composable
fun MinimalMealCard(meal: Meal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            ,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(meal.time_of_day, style = MaterialTheme.typography.titleSmall)
            Text(meal.food_recommended, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
