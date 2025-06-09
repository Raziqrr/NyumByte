/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-08 22:32:26
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-08 23:53:39
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/common/RememberCheckBox.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RememberCheckBox(
    checked: Boolean,
    onChange: (Boolean)->Unit,
    
){
    Row(
        modifier = Modifier.
        fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onChange,
        )
        Text("Remember me")
    }
}

@Preview(showBackground = true)
@Composable
fun RememberCheckBoxPreview() {
    val checkedState = remember { mutableStateOf(true) }

    RememberCheckBox(
        checked = checkedState.value,
        onChange = { checkedState.value = it }
    )
}