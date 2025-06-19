package com.example.nyumbyte.data.network.gemini

import com.example.nyumbyte.ui.screens.health.GeminiRequest
import com.example.nyumbyte.ui.screens.health.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
private const val API_KEY = "AIzaSyAZlFNUBS2898eUjh8JjCK9Jb8bE8xoIrQ" // ⚠ Replace with env-secure key in production

interface GeminiService {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-pro:generateContent")
    fun analyzeHealth(
        @Body request: GeminiRequest,
        @retrofit2.http.Query("key") apiKey: String
    ): Call<GeminiResponse>
}


object GeminiApi {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    val retrofitService: GeminiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiService::class.java)
    }
}
