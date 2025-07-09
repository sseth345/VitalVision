package com.example.detect

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.objects.DetectedObject
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    detectionMode: String,
    onBackPressed: () -> Unit,
    isDarkMode: Boolean
) {
    println("🔥 CAMERA SCREEN - Mode: $detectionMode")
    Log.d("CameraScreen", "🔥 CAMERA SCREEN - Mode: $detectionMode")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )
    var faces by remember { mutableStateOf<List<Face>>(emptyList()) }
    var objects by remember { mutableStateOf<List<DetectedObject>>(emptyList()) }
    var currentImageProxy by remember { mutableStateOf<ImageProxy?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf<String?>(null) }
    var isBackCamera by remember { mutableStateOf(true) }

    val storage = remember { SimpleStorage(context) }
    val backgroundColor = if (isDarkMode) Color(0xFF1A1A2E) else Color(0xFFF8F9FA)

    // Auto request permission
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            println("🔥 REQUESTING CAMERA PERMISSION")
            Log.d("CameraScreen", "🔥 REQUESTING CAMERA PERMISSION")
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            // Camera Preview
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                detectionMode = detectionMode,
                isBackCamera = isBackCamera,
                onFacesDetected = { detectedFaces, imageProxy ->
                    println("🔥 FACES DETECTED: ${detectedFaces.size}")
                    Log.d("CameraScreen", "🔥 FACES DETECTED: ${detectedFaces.size}")
                    faces = detectedFaces
                    currentImageProxy = imageProxy
                },
                onObjectsDetected = { detectedObjects ->
                    println("🔥 OBJECTS DETECTED: ${detectedObjects.size}")
                    Log.d("CameraScreen", "🔥 OBJECTS DETECTED: ${detectedObjects.size}")
                    objects = detectedObjects
                }
            )

            // Detection Overlay
            DetectionOverlay(
                faces = if (detectionMode == "face") faces else emptyList(),
                objects = if (detectionMode == "object") objects else emptyList(),
                modifier = Modifier.fillMaxSize()
            )

            // Enhanced Top Bar (36dp नीचे shifted)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 52.dp, bottom = 16.dp), // Changed from 16dp to 52dp (36dp shift)
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) Color(0xFF2D2D44) else Color.White
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDarkMode) Color.White else Color.Black
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (detectionMode == "face") "😊 Scanning" else "🚗 Scanning", // Changed: Face emoji + "Scanning" text, Car emoji for object
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color.Black
                        )
                        Text(
                            text = "Detected: ${if (detectionMode == "face") faces.size else objects.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = {
                            isBackCamera = !isBackCamera
                            println("🔥 CAMERA SWITCHED - Back Camera: $isBackCamera")
                            Log.d("CameraScreen", "🔥 CAMERA SWITCHED - Back Camera: $isBackCamera")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh, // Camera switch icon
                            contentDescription = "Switch Camera",
                            tint = if (isDarkMode) Color.White else Color.Black
                        )
                    }
                }
            }

            // Enhanced Save Button (ONLY for faces, NOT for objects)
            if (detectionMode == "face" && faces.isNotEmpty()) {

                println("🔥 SHOWING SAVE BUTTON FOR FACE")
                Log.d("CameraScreen", "🔥 SHOWING SAVE BUTTON FOR FACE")

                FloatingActionButton(
                    onClick = {
                        println("🔥 SAVE BUTTON CLICKED - Mode: $detectionMode")
                        Log.d("CameraScreen", "🔥 SAVE BUTTON CLICKED - Mode: $detectionMode")
                        showSaveDialog = true
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Save",
                        tint = Color.White
                    )
                }
            }

            // Result Message
            showResult?.let { message ->
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkMode) Color(0xFF2D2D44) else Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (message.contains("✅")) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = "Result",
                            tint = if (message.contains("✅")) Color.Green else Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showResult = null }) {
                            Text("OK")
                        }
                    }
                }
            }
        }

        // Enhanced Save Dialog - WORKING VERSION
        if (showSaveDialog) {
            SaveDialog(
                onSave = { name ->
                    currentImageProxy?.let { imageProxy ->
                        scope.launch {
                            try {
                                println("🔥 SAVING: $name, Mode: $detectionMode")
                                Log.d("CameraScreen", "🔥 SAVING: $name, Mode: $detectionMode")

                                // Convert before closing
                                val bitmap = imageProxyToBitmap(imageProxy)

                                if (bitmap == null) {
                                    showResult = "❌ Failed to process image"
                                    return@launch
                                }

                                // Simple cropping for faces only
                                val finalBitmap = if (detectionMode == "face" && faces.isNotEmpty()) {
                                    cropFaceFromBitmap(bitmap, faces.first().boundingBox)
                                } else {
                                    bitmap
                                }

                                println("🔥 Saving bitmap: ${finalBitmap.width}x${finalBitmap.height}")

                                val savedId = storage.saveItem(finalBitmap, name, detectionMode)
                                if (savedId != null) {
                                    showResult = "✅ $name saved successfully!"
                                    println("🎉 SAVED: $name as $detectionMode")
                                } else {
                                    showResult = "❌ Save failed. Please try again."
                                    println("❌ SAVE FAILED: $name")
                                }
                            } catch (e: Exception) {
                                showResult = "❌ Error: ${e.message}"
                                Log.e("CameraScreen", "❌ Save error", e)
                            } finally {
                                try {
                                    imageProxy.close()
                                } catch (e: Exception) {
                                    Log.e("CameraScreen", "Error closing imageProxy", e)
                                }
                            }
                        }
                    }
                    showSaveDialog = false
                },
                onDismiss = { showSaveDialog = false },
                isDarkMode = isDarkMode
            )
        }
    } else {
        // Enhanced Permission Request
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) Color(0xFF2D2D44) else Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Camera",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "📷 Camera Permission Required",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This app needs camera access for face and object detection",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkMode) Color.White.copy(0.7f) else Color.Black.copy(0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            println("🔥 GRANT PERMISSION CLICKED")
                            Log.d("CameraScreen", "🔥 GRANT PERMISSION CLICKED")
                            cameraPermissionState.launchPermissionRequest()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant Camera Permission")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = onBackPressed) {
                        Text("Go Back")
                    }
                }
            }
        }
    }
}

@Composable
fun SaveDialog(
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
    isDarkMode: Boolean
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "💾 Save Face",
                color = if (isDarkMode) Color.White else Color.Black
            )
        },
        text = {
            Column {
                Text(
                    "Enter a name for this face:",
                    color = if (isDarkMode) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    placeholder = { Text("e.g. John Doe") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim())
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = if (isDarkMode) Color(0xFF2D2D44) else Color.White
    )
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    detectionMode: String,
    isBackCamera: Boolean,
    onFacesDetected: (List<Face>, ImageProxy) -> Unit,
    onObjectsDetected: (List<DetectedObject>) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }

    val cameraSelector = if (isBackCamera) {
        CameraSelector.DEFAULT_BACK_CAMERA
    } else {
        CameraSelector.DEFAULT_FRONT_CAMERA
    }

    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }

    val imageAnalyzer = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also {
            it.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                if (detectionMode == "face") {
                    FaceDetectionAnalyzer { faces, imageProxy ->
                        onFacesDetected(faces, imageProxy)
                    }
                } else {
                    ObjectDetectionAnalyzer { objects ->
                        onObjectsDetected(objects)
                    }
                }
            )
        }

    LaunchedEffect(detectionMode, isBackCamera) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalyzer
        )
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

// Enhanced bitmap conversion - FIXED VERSION
private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
    return try {
        if (imageProxy.format == android.graphics.ImageFormat.YUV_420_888) {
            val yBuffer = imageProxy.planes[0].buffer
            val uBuffer = imageProxy.planes[1].buffer
            val vBuffer = imageProxy.planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)

            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            val yuvImage = android.graphics.YuvImage(
                nv21,
                android.graphics.ImageFormat.NV21,
                imageProxy.width,
                imageProxy.height,
                null
            )

            val out = java.io.ByteArrayOutputStream()
            yuvImage.compressToJpeg(
                android.graphics.Rect(0, 0, imageProxy.width, imageProxy.height),
                90,
                out
            )

            val imageBytes = out.toByteArray()
            val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            if (bitmap != null) {
                println("✅ YUV Bitmap created: ${bitmap.width}x${bitmap.height}")
                Log.d("CameraScreen", "✅ YUV Bitmap created: ${bitmap.width}x${bitmap.height}")
                return bitmap
            }
        }

        val buffer = imageProxy.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        if (bitmap == null) {
            println("❌ Failed to decode bitmap from byte array")
            Log.e("CameraScreen", "❌ Failed to decode bitmap")
            return null
        }

        println("✅ Simple Bitmap created: ${bitmap.width}x${bitmap.height}")
        Log.d("CameraScreen", "✅ Simple Bitmap created: ${bitmap.width}x${bitmap.height}")

        return bitmap

    } catch (e: Exception) {
        println("❌ Bitmap conversion error: ${e.message}")
        Log.e("CameraScreen", "❌ Bitmap conversion error", e)
        return null
    }
}

private fun cropFaceFromBitmap(bitmap: Bitmap, boundingBox: Rect): Bitmap {
    return try {
        val padding = 50
        val left = maxOf(0, boundingBox.left - padding)
        val top = maxOf(0, boundingBox.top - padding)
        val right = minOf(bitmap.width, boundingBox.right + padding)
        val bottom = minOf(bitmap.height, boundingBox.bottom + padding)
        val width = right - left
        val height = bottom - top

        if (width > 0 && height > 0) {
            Bitmap.createBitmap(bitmap, left, top, width, height)
        } else {
            println("⚠️ Invalid crop dimensions, returning original bitmap")
            bitmap
        }
    } catch (e: Exception) {
        println("❌ Crop error: ${e.message}")
        Log.e("CameraScreen", "❌ Crop error", e)
        bitmap
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }
