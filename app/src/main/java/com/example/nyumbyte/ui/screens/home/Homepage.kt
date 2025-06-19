/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-08 01:41:59
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-19 20:33:09
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/home/Homepage.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.nyumbyte.ui.navigation.Screens

@Composable
fun Homepage(
    navController: NavHostController
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
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
                Text(
                    "25020390"
                )
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
                    Text("Your meal for today")
                    TextButton(
                        onClick = {
                            navController.navigate(Screens.DietPlans.name
                            )
                        },
                    ) { Text("View Meal Plans")}
                }
                Card { 
                    Column { 
                        Text("Breakfast")
                        Text("Something")
                    }
                }
                Card {
                    Column {
                        Text("Lunch")
                        Text("Something")
                    }
                }
                Card {
                    Column {
                        Text("Dinner")
                        Text("Something")
                    }
                }
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