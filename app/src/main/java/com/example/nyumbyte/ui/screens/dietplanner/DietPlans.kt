/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-09 17:15:33
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-20 06:39:21
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/dietplanner/DietPlans.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.dietplanner

import PrimaryButton
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nyumbyte.data.model.Meal
import com.example.nyumbyte.data.network.firebase.UserUiState
import com.example.nyumbyte.data.network.firebase.UserViewModel
import com.example.nyumbyte.ui.navigation.Screens
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import com.example.nyumbyte.ui.beta.LiquidGlass
import com.example.nyumbyte.ui.beta.LiquidGlassConfig
import com.example.nyumbyte.ui.beta.LiquidGlassText
import com.example.nyumbyte.ui.common.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietPlan(
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    dietPlanViewModel: DietPlanViewModel,
    navController: NavController,
    onGenerateClick: () -> Unit = {}
) {
    val userState by userViewModel.userUiState.collectAsState()
    val user = (userState as? UserUiState.Success)?.user
    val dietPlans = user?.dietPlan ?: emptyList()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "My Diet Plan",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Box(
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                PrimaryButton(
                    onClick = {
                        navController.navigate(Screens.CreateDietPlan.name)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    text = "Generate New Diet Plan",
                )
            }
        },
        

    ) { innerPadding ->

        // Scrollable content, with padding to avoid being hidden behind top/bottom bars
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (dietPlans.isEmpty()) {
                item {
                    Text(
                        "No diet plan available.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                item { 
                    Spacer(modifier = Modifier.height(30.dp))
                }
                items(dietPlans) { plan ->
                    ExpandableDietPlanCard(plan)
                }
            }

            // Extra bottom spacer so last item doesn't hide behind button
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

