/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 23:40:58
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-06 23:41:07
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/common/GenderSelector.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GenderSelector(
    selectedGender: String,
    onGenderSelected: (String) -> Unit
) {
    val genderOptions = listOf("Male", "Female", "Other")

    Column {
        Text("Gender", style = MaterialTheme.typography.labelLarge)
        genderOptions.forEach { gender ->
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = gender == selectedGender,
                    onClick = { onGenderSelected(gender) }
                )
                Text(text = gender)
            }
        }
    }
}