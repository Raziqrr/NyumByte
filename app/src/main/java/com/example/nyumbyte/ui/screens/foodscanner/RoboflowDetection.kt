package com.example.nyumbyte.ui.screens.foodscanner


import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

fun detectFood(bitmap: Bitmap, context: Context, onResult: (String) -> Unit) {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val byteArray = stream.toByteArray()
    val encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP)

    val url = "https://detect.roboflow.com/fooddetection-con8e/3?api_key=PkJbfkWwyj0Ms6IYS1Bl"

    val body = MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart(
            "file", "image.jpg",
            byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
        ).build()

    val request = Request.Builder().url(url).post(body).build()
    val client = OkHttpClient()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            client.newCall(request).execute().use { response ->
                val json = JSONObject(response.body?.string() ?: "")
                val predictions = json.optJSONArray("predictions")
                val label = if (predictions?.length() ?: 0 > 0)
                    predictions!!.getJSONObject(0).getString("class")
                else "Unknown"
                withContext(Dispatchers.Main) {
                    onResult(label)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
