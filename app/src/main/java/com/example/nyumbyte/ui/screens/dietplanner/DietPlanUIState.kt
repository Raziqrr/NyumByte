/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-05-15 19:20:33
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-10 00:51:21
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/dietplanner/DietPlanUIState.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.dietplanner
import com.example.nyumbyte.data.model.DietPlan

data class DietPlanUiState(
    val dietPlans: List<DietPlan> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isSaved: Boolean = false, // e.g., saved to Firebase
    val lastUpdated: Long? = null, // Unix timestamp of last update
    val errorMessage: String? = null,
) {
    val hasError: Boolean get() = errorMessage != null
    val isEmpty: Boolean get() = dietPlans.isEmpty() && !isLoading
}
