package com.example.nyumbyte.ui.screens.foodscanner


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.nyumbyte.ui.screens.foodscanner.detectFood
import java.io.File

@Composable
fun ScanScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var isCapturing by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Optional semi-transparent overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
        )

        // Capture Button or Loading Spinner
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedContent(
                targetState = isCapturing,
                label = "CaptureState"
            ) { capturing ->
                if (capturing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color.White,
                        strokeWidth = 4.dp
                    )
                } else {
                    Button(
                        onClick = {
                            isCapturing = true
                            val file = File(context.cacheDir, "image.jpg")
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                            imageCapture?.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        try {
                                            // Decode original image
                                            val originalBitmap = BitmapFactory.decodeFile(file.absolutePath)

                                            // Compress and overwrite original file
                                            file.outputStream().use { outStream ->
                                                originalBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outStream)
                                            }

                                            // Decode compressed image
                                            val compressedBitmap = BitmapFactory.decodeFile(file.absolutePath)

                                            // Send to detection
                                            detectFood(compressedBitmap, context) { label ->
                                                isCapturing = false
                                                navController.navigate("result/$label")
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Image processing failed", Toast.LENGTH_SHORT).show()
                                            isCapturing = false
                                        }
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Toast.makeText(context, "Capture failed", Toast.LENGTH_SHORT).show()
                                        isCapturing = false
                                    }
                                }
                            )
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.9f),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Capture")
                    }
                }
            }
        }
    }

    // Camera Initialization
    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture!!
            )
        } catch (exc: Exception) {
            Toast.makeText(context, "Camera init failed", Toast.LENGTH_SHORT).show()
        }
    }
}
