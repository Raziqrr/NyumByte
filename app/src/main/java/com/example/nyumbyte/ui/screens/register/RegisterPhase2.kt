/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-09 13:53:37
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-19 16:05:40
 * @FilePath: app/src/main/java/com/example/nyumbyte/ui/screens/register/RegisterPhase2.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.ui.screens.register

import AuthViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nyumbyte.data.model.User
import com.example.nyumbyte.data.network.firebase.FirestoreRepository
import com.example.nyumbyte.ui.common.AccountTextField
import com.example.nyumbyte.ui.common.EthnicityDropdown
import com.example.nyumbyte.ui.common.GenderSelector
import com.example.nyumbyte.ui.navigation.Screens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color


@Composable
fun RegisterPhase2(
    authViewModel: AuthViewModel,
    navController: NavController,
) {
    var userName by remember { mutableStateOf("") }
    var ethnicity by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var loadingMessage by remember { mutableStateOf("Saving profile...") }

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var shouldNavigate by remember { mutableStateOf(false) }
    val authState by authViewModel.authUiState.collectAsState()
    val uid = authState.user?.uid
    var hasNavigated by remember { mutableStateOf(false) }


    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate && !hasNavigated) {
            hasNavigated = true
            delay(1000)
            navController.navigate(Screens.RegisterSuccess.name) {
                popUpTo(Screens.RegisterDetails.name) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Phase 2: Profile Details", style = MaterialTheme.typography.titleLarge)

        AccountTextField(
            value = userName,
            onValueChange = { userName = it },
            label = "Username",
            leadingIcon = Icons.Default.Person,
            isValid = userName.length >= 3
        )

        EthnicityDropdown(
            selectedEthnicity = ethnicity,
            onEthnicitySelected = { ethnicity = it },
            modifier = Modifier.fillMaxWidth()
        )

        GenderSelector(
            selectedGender = gender,
            onGenderSelected = { gender = it }
        )

        AccountTextField(
            value = age,
            onValueChange = { age = it },
            label = "Age",
            leadingIcon = Icons.Default.Numbers,
            isValid = age.toIntOrNull()?.let { it in 1..120 } == true
        )

        AccountTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = "Phone Number",
            leadingIcon = Icons.Default.Phone,
            isValid = phoneNumber.length in 10..15
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val trimmedUserName = userName.trim()
                val trimmedPhone = phoneNumber.trim()

                val isFormValid = isValidProfile(
                    userName = trimmedUserName,
                    age = age,
                    phone = trimmedPhone,
                    gender = gender,
                    ethnicity = ethnicity
                )

                if (!isFormValid) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Please fill in all fields correctly.")
                    }
                    return@Button
                }

                if (uid != null) {
                    val user = User(
                        id = uid!!,
                        userName = trimmedUserName,
                        age = age.toIntOrNull() ?: 0,
                        phoneNumber = trimmedPhone,
                        weight = 0.0,
                        height = 0.0,
                        allergies = listOf(),
                        gender = gender,
                        ethnicity = ethnicity,
                        level = 1,
                        exp = 0,
                        totalPoints = 0,
                        friends = listOf(),
                        dietPlan = listOf(),
                    )

                    isLoading = true
                    loadingMessage = "Saving profile..."

                    coroutineScope.launch {
                        val success = FirestoreRepository.saveUserData(uid!!, user.toMap())
                        isLoading = false
                        if (success) {
                            Log.d("RegisterPhase2", "User data saved.")
                            authViewModel.clearUser()
                            loadingMessage = "Redirecting to login..."
                            shouldNavigate = true
                        } else {
                            Log.e("RegisterPhase2", "Failed to save user data.")
                            snackbarHostState.showSnackbar(
                                message = "Failed to save profile. Try again.",
                                withDismissAction = true
                            )
                            Toast.makeText(
                                context,
                                "Failed to save profile",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Log.e("RegisterPhase2", "UID is null.")
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "User ID not found.",
                            withDismissAction = true
                        )
                    }
                }
            },
            modifier = Modifier.align(Alignment.End),
            enabled = uid != null && !isLoading
        ) {
            Text("Register")
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color.White)
                Text(
                    text = loadingMessage,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}

private fun isValidProfile(
    userName: String,
    age: String,
    phone: String,
    gender: String,
    ethnicity: String
): Boolean {
    return userName.length >= 3 &&
            age.toIntOrNull()?.let { it in 1..120 } == true &&
            phone.length in 10..15 &&
            gender.isNotBlank() &&
            ethnicity.isNotBlank()
}



