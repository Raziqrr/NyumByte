/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 23:41:19
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-06 23:41:32
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/common/EthnicityDropdown.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.common

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EthnicityDropdown(
    selectedEthnicity: String,
    onEthnicitySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Asian", "Black", "Caucasian", "Hispanic", "Mixed", "Other")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selectedEthnicity,
            onValueChange = {},
            readOnly = true,
            label = { Text("Ethnicity") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onEthnicitySelected(label)
                        expanded = false
                    }
                )
            }
        }
    }
}