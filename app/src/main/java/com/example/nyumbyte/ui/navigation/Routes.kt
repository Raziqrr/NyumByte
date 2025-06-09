/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-06 01:05:24
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-08 22:58:59
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
    Home(title = (R.string.home)),
}