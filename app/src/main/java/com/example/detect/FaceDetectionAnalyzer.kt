package com.example.detect

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import android.util.Log

class FaceDetectionAnalyzer(
    private val onFacesDetected: (List<Face>, ImageProxy) -> Unit
) : ImageAnalysis.Analyzer {

    // FIXED: Disable tracking for real-time updates
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST) // Faster for real-time
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.1f) // Smaller for better detection
        // .enableTracking() // REMOVED - tracking causes fixed boxes
        .build()

    private val detector = FaceDetection.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            detector.process(image)
                .addOnSuccessListener { faces ->
                    println("🔥 REAL-TIME FACES: ${faces.size}")
                    Log.d("FaceAnalyzer", "🔥 REAL-TIME FACES: ${faces.size}")

                    // No caching - fresh detection every time
                    val validFaces = faces.filter { face ->
                        val boundingBox = face.boundingBox
                        val width = boundingBox.width()
                        val height = boundingBox.height()
                        width > 30 && height > 30 // Smaller minimum for better detection
                    }

                    validFaces.forEachIndexed { index, face ->
                        val emotion = analyzeEmotionRealTime(face)
                        println("🔥 Face $index EMOTION: $emotion")
                        println("🔥 Face $index BOX: ${face.boundingBox}")
                        println("🔥 Face $index SMILE: ${face.smilingProbability}")
                        println("🔥 Face $index EYES: L=${face.leftEyeOpenProbability}, R=${face.rightEyeOpenProbability}")
                        Log.d("FaceAnalyzer", "🔥 Face $index EMOTION: $emotion")
                    }

                    // Always pass fresh faces
                    onFacesDetected(validFaces, imageProxy)
                }
                .addOnFailureListener { exception ->
                    println("❌ Face detection failed: ${exception.message}")
                    Log.e("FaceAnalyzer", "❌ Detection failed", exception)
                    onFacesDetected(emptyList(), imageProxy)
                }
        } else {
            onFacesDetected(emptyList(), imageProxy)
        }
    }

    // Real-time emotion analysis with better thresholds
    private fun analyzeEmotionRealTime(face: Face): String {
        val smilingProb = face.smilingProbability ?: 0f
        val leftEyeOpen = face.leftEyeOpenProbability ?: 1f
        val rightEyeOpen = face.rightEyeOpenProbability ?: 1f
        val avgEyeOpen = (leftEyeOpen + rightEyeOpen) / 2f

        return when {
            // More sensitive thresholds for real-time detection
            smilingProb > 0.6f -> "😊 Happy"
            smilingProb < 0.2f && avgEyeOpen > 0.7f -> "😢 Sad"
            avgEyeOpen > 0.95f && smilingProb > 0.3f && smilingProb < 0.6f -> "😲 Surprised"
            smilingProb < 0.15f && avgEyeOpen > 0.5f && avgEyeOpen < 0.8f -> "😠 Angry"
            avgEyeOpen < 0.3f -> "😴 Sleepy"
            else -> "😐 Neutral"
        }
    }
}





