//package com.example.nyumbyte.ui.screens.foodscanner
//
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import okhttp3.*
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONArray
//import org.json.JSONObject
//
//data class GeminiResult(
//    val calorie: String,
//    val suggestion: String
//)
//
//suspend fun fetchGeminiSuggestion(label: String): GeminiResult {
//    val prompt = """
//        For the food "$label":
//        1. Estimate its calorie content in kilocalories (kcal). Be specific and give a single number only (no ranges, no words like 'about', no explanation).
//        2. Suggest 2 healthier alternative in the same food category, with a not so short not so long reason why it's healthier.
//
//        Format your answer like:
//        Calories: <number> kcal
//        Suggestions:
//        1. <food name> – <short description>
//        2. <food name> – <short description>
//    """.trimIndent()
//
//    val json = JSONObject()
//    val content = JSONObject()
//    content.put("parts", JSONArray().put(JSONObject().put("text", prompt)))
//    json.put("contents", JSONArray().put(content))
//
//    val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
//    val request = Request.Builder()
//        .url("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent?key=AIzaSyAjki-horKZfIdi9TQ0FSPExmhXNtIwHVk")
//        .post(requestBody)
//        .build()
//
//    val client = OkHttpClient()
//
//    return withContext(Dispatchers.IO) {
//        try {
//            client.newCall(request).execute().use { response ->
//                if (!response.isSuccessful) {
//                    throw Exception("Unexpected response code ${response.code}")
//                }
//
//                val body = response.body?.string() ?: ""
//                val text = JSONObject(body)
//                    .getJSONArray("candidates")
//                    .getJSONObject(0)
//                    .getJSONObject("content")
//                    .getJSONArray("parts")
//                    .getJSONObject(0)
//                    .getString("text")
//
//                // Extract calorie number
//                val calorieLine = text.lines().find { it.contains("Calories:", ignoreCase = true) } ?: "Calories: 0"
//                val calorie = Regex("""\d+""").find(calorieLine)?.value ?: "0"
//
//                // Extract suggestion
//                val suggestionStart = text.indexOf("Suggestions:")
//                val suggestion = if (suggestionStart != -1) {
//                    text.substring(suggestionStart).replace("Suggestions:", "").trim()
//                } else {
//                    "No suggestions found."
//                }
//
//                GeminiResult(calorie = calorie, suggestion = suggestion)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            GeminiResult(calorie = "0", suggestion = "No suggestions found.")
//        }
//    }
//}
