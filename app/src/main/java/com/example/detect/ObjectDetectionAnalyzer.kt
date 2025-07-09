//package com.example.detect
//
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.ImageProxy
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.objects.DetectedObject
//import com.google.mlkit.vision.objects.ObjectDetection
//import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
//import android.util.Log
//
//class ObjectDetectionAnalyzer(
//    private val onObjectsDetected: (List<DetectedObject>) -> Unit
//) : ImageAnalysis.Analyzer {
//
//    private val options = ObjectDetectorOptions.Builder()
//        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
//        .enableMultipleObjects()
//        .enableClassification()
//        .build()
//
//    private val detector = ObjectDetection.getClient(options)
//
//    override fun analyze(imageProxy: ImageProxy) {
//        val mediaImage = imageProxy.image
//        if (mediaImage != null) {
//            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//
//            detector.process(image)
//                .addOnSuccessListener { objects ->
//                    println("🔥 OBJECT ANALYZER - Detected: ${objects.size}")
//                    Log.d("ObjectAnalyzer", "🔥 Detected: ${objects.size}")
//
//                    // Log each object
//                    objects.forEach { obj ->
//                        val bestLabel = obj.labels.maxByOrNull { it.confidence }
//                        println("🔥 OBJECT: ${bestLabel?.text} (${((bestLabel?.confidence ?: 0f) * 100).toInt()}%)")
//                    }
//
//                    onObjectsDetected(objects)
//                }
//                .addOnFailureListener { exception ->
//                    println("❌ Object detection failed: ${exception.message}")
//                    Log.e("ObjectAnalyzer", "❌ Detection failed", exception)
//                    onObjectsDetected(emptyList())
//                }
//                .addOnCompleteListener {
//                    imageProxy.close()
//                }
//        } else {
//            imageProxy.close()
//        }
//    }
//}
//package com.example.detect
//
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.ImageProxy
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.objects.DetectedObject
//import com.google.mlkit.vision.objects.ObjectDetection
//import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
//import android.util.Log
//
//class ObjectDetectionAnalyzer(
//    private val onObjectsDetected: (List<DetectedObject>) -> Unit
//) : ImageAnalysis.Analyzer {
//
//    // Search results के according - SINGLE_IMAGE_MODE for better accuracy
//    private val options = ObjectDetectorOptions.Builder()
//        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
//        .enableMultipleObjects()
//        .enableClassification()
//        .build()
//
//    private val detector = ObjectDetection.getClient(options)
//
//    override fun analyze(imageProxy: ImageProxy) {
//        val mediaImage = imageProxy.image
//        if (mediaImage != null) {
//            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//
//            detector.process(image)
//                .addOnSuccessListener { objects ->
//                    println("🔥 RAW OBJECTS: ${objects.size}")
//                    Log.d("ObjectAnalyzer", "🔥 RAW OBJECTS: ${objects.size}")
//
//                    // Simple filtering
//                    val validObjects = objects.filter { obj ->
//                        val bestLabel = obj.labels.maxByOrNull { it.confidence }
//                        (bestLabel?.confidence ?: 0f) > 0.5f
//                    }
//
//                    validObjects.forEach { obj ->
//                        val bestLabel = obj.labels.maxByOrNull { it.confidence }
//                        println("🔥 OBJECT: ${bestLabel?.text} (${((bestLabel?.confidence ?: 0f) * 100).toInt()}%)")
//                    }
//
//                    println("🔥 VALID OBJECTS: ${validObjects.size}")
//                    Log.d("ObjectAnalyzer", "🔥 VALID OBJECTS: ${validObjects.size}")
//
//                    onObjectsDetected(validObjects)
//                }
//                .addOnFailureListener { exception ->
//                    println("❌ Object detection failed: ${exception.message}")
//                    Log.e("ObjectAnalyzer", "❌ Detection failed", exception)
//                    onObjectsDetected(emptyList())
//                }
//                .addOnCompleteListener {
//                    imageProxy.close()
//                }
//        } else {
//            imageProxy.close()
//        }
//    }
//}

package com.example.detect

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import android.util.Log

class ObjectDetectionAnalyzer(
    private val onObjectsDetected: (List<DetectedObject>) -> Unit
) : ImageAnalysis.Analyzer {

    // Your working configuration
    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableMultipleObjects()
        .enableClassification()
        .build()

    private val detector = ObjectDetection.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            detector.process(image)
                .addOnSuccessListener { objects ->
                    // Add logging for debugging
                    println("🔥 OBJECTS DETECTED: ${objects.size}")
                    Log.d("ObjectAnalyzer", "🔥 OBJECTS DETECTED: ${objects.size}")

                    objects.forEach { obj ->
                        val bestLabel = obj.labels.maxByOrNull { it.confidence }
                        println("🔥 OBJECT: ${bestLabel?.text} (${((bestLabel?.confidence ?: 0f) * 100).toInt()}%)")
                        Log.d("ObjectAnalyzer", "🔥 OBJECT: ${bestLabel?.text}")
                    }

                    onObjectsDetected(objects)
                }
                .addOnFailureListener { exception ->
                    println("❌ Object detection failed: ${exception.message}")
                    Log.e("ObjectAnalyzer", "❌ Object detection failed", exception)
                    exception.printStackTrace()
                    onObjectsDetected(emptyList())
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}




