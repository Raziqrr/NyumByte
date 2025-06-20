/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 01:05:24
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-20 08:50:20
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/navigation/Routes.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.navigation

import androidx.annotation.StringRes
import com.example.nyumbyte.R

enum class Screens(@StringRes val title: Int){
    SplashScreen(title = (R.string.nyumbyte)),
    Register(title = (R.string.register)),
    RegisterDetails(title = (R.string.register_details)),
    RegisterSuccess(title = (R.string.successful_registration)),
    Login(title = (R.string.login)),
    HomeMain(title = (R.string.nyumbyte_home)),
    DietPlans(title = (R.string.diet_plans)),
    CreateDietPlan(title = (R.string.create_diet_plan)),
    DietPlanResult(title = (R.string.diet_plan_result)),
    Home(title = (R.string.home)),
    Broco(title = (R.string.broco)),
    ChallengePage(title = (R.string.challenge_page)),
    SocialPage(title = (R.string.social)),
    RewardsPage(title = (R.string.rewards_page)),
    Profile(title = (R.string.profile)),
    HealthAnalytics(title = R.string.health_analytics),

}