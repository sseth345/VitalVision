//
//package com.example.detect
//
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.nativeCanvas
//import androidx.compose.ui.graphics.toArgb
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.unit.dp
//import com.google.mlkit.vision.face.Face
//import com.google.mlkit.vision.objects.DetectedObject
//import android.util.Log
//
//@Composable
//fun DetectionOverlay(
//    faces: List<Face> = emptyList(),
//    objects: List<DetectedObject> = emptyList(),
//    modifier: Modifier = Modifier
//) {
//    println("🔥 OVERLAY REFRESH - Faces: ${faces.size}, Objects: ${objects.size}")
//    Log.d("DetectionOverlay", "🔥 OVERLAY REFRESH - Faces: ${faces.size}, Objects: ${objects.size}")
//
//    val density = LocalDensity.current
//    val strokeWidth = with(density) { 3.dp.toPx() }
//    val canvasTextSize = with(density) { 16.dp.toPx() }
//
//    Canvas(modifier = modifier) {
//        val canvasWidth = size.width
//        val canvasHeight = size.height
//
//        println("🔥 Canvas: ${canvasWidth}x${canvasHeight}")
//
//        // DYNAMIC FACE DETECTION - Real-time box resizing
//        faces.forEachIndexed { index, face ->
//            val boundingBox = face.boundingBox
//
//            // Better scaling for face coordinates
//            val scaleX = canvasWidth / 640f
//            val scaleY = canvasHeight / 480f
//
//            // DYNAMIC COORDINATES - calculated fresh every time
//            val faceLeft = boundingBox.left.toFloat() * scaleX
//            val faceTop = boundingBox.top.toFloat() * scaleY
//            val faceRight = boundingBox.right.toFloat() * scaleX
//            val faceBottom = boundingBox.bottom.toFloat() * scaleY
//
//            println("🔥 Face $index: Raw(${boundingBox.left}, ${boundingBox.top}, ${boundingBox.right}, ${boundingBox.bottom})")
//            println("🔥 Face $index: Scaled(${faceLeft}, ${faceTop}, ${faceRight}, ${faceBottom})")
//
//            // Ensure bounds
//            val adjustedLeft = maxOf(0f, minOf(faceLeft, canvasWidth - 10))
//            val adjustedTop = maxOf(0f, minOf(faceTop, canvasHeight - 10))
//            val adjustedRight = maxOf(adjustedLeft + 10, minOf(faceRight, canvasWidth))
//            val adjustedBottom = maxOf(adjustedTop + 10, minOf(faceBottom, canvasHeight))
//
//            val boxWidth = adjustedRight - adjustedLeft
//            val boxHeight = adjustedBottom - adjustedTop
//
//            println("🔥 Face $index: Final Box(${adjustedLeft}, ${adjustedTop}) Size(${boxWidth}x${boxHeight})")
//
//            if (boxWidth > 5 && boxHeight > 5) {
//                // DYNAMIC COLOR based on emotion
//                val emotion = analyzeEmotionRealTime(face)
//                val boxColor = when {
//                    emotion.contains("Happy") -> Color.Green
//                    emotion.contains("Sad") -> Color.Blue
//                    emotion.contains("Angry") -> Color.Red
//                    emotion.contains("Surprised") -> Color.Yellow
//                    emotion.contains("Sleepy") -> Color.Magenta
//                    else -> Color.White
//                }
//
//                drawRect(
//                    color = boxColor,
//                    topLeft = androidx.compose.ui.geometry.Offset(adjustedLeft, adjustedTop),
//                    size = androidx.compose.ui.geometry.Size(boxWidth, boxHeight),
//                    style = Stroke(width = strokeWidth)
//                )
//
//                val label = "Face: $emotion"
//
//                drawRect(
//                    color = boxColor.copy(alpha = 0.8f),
//                    topLeft = androidx.compose.ui.geometry.Offset(adjustedLeft, maxOf(adjustedTop - canvasTextSize - 5, 0f)),
//                    size = androidx.compose.ui.geometry.Size(label.length * canvasTextSize * 0.6f, canvasTextSize + 10)
//                )
//                drawContext.canvas.nativeCanvas.drawText(
//                    label,
//                    adjustedLeft,
//                    maxOf(adjustedTop - 10, canvasTextSize),
//                    android.graphics.Paint().apply {
//                        color = Color.White.toArgb()
//                        textSize = canvasTextSize
//                        isAntiAlias = true
//                        isFakeBoldText = true
//                    }
//                )
//
//                // Center dot for debugging
//                val centerX = adjustedLeft + boxWidth / 2
//                val centerY = adjustedTop + boxHeight / 2
//                drawCircle(
//                    color = Color.Red,
//                    radius = 4f,
//                    center = androidx.compose.ui.geometry.Offset(centerX, centerY)
//                )
//            }
//        }
//
//        // Objects (keep as is)
//        objects.forEachIndexed { index, obj ->
//            val boundingBox = obj.boundingBox
//
//            val scaleX = canvasWidth / 1080f
//            val scaleY = canvasHeight / 1920f
//
//            val objLeft = maxOf(0f, boundingBox.left * scaleX)
//            val objTop = maxOf(0f, boundingBox.top * scaleY)
//            val objRight = minOf(canvasWidth, boundingBox.right * scaleX)
//            val objBottom = minOf(canvasHeight, boundingBox.bottom * scaleY)
//
//            if (objLeft >= 0 && objTop >= 0 && objRight <= canvasWidth && objBottom <= canvasHeight) {
//                drawRect(
//                    color = Color.Blue,
//                    topLeft = androidx.compose.ui.geometry.Offset(objLeft, objTop),
//                    size = androidx.compose.ui.geometry.Size(objRight - objLeft, objBottom - objTop),
//                    style = Stroke(width = strokeWidth)
//                )
//
//                val bestLabel = obj.labels.maxByOrNull { it.confidence }
//                val objectName = bestLabel?.text ?: "Object"
//                val confidence = ((bestLabel?.confidence ?: 0f) * 100).toInt()
//                val objectLabel = "$objectName ($confidence%)"
//
//                drawContext.canvas.nativeCanvas.drawText(
//                    objectLabel,
//                    objLeft,
//                    maxOf(objTop - 10, canvasTextSize),
//                    android.graphics.Paint().apply {
//                        color = Color.White.toArgb()
//                        textSize = canvasTextSize
//                        isAntiAlias = true
//                        isFakeBoldText = true
//                    }
//                )
//            }
//        }
//    }
//}
//private fun analyzeEmotionRealTime(face: Face): String {
//    val smilingProb = face.smilingProbability ?: 0f
//    val leftEyeOpen = face.leftEyeOpenProbability ?: 1f
//    val rightEyeOpen = face.rightEyeOpenProbability ?: 1f
//    val avgEyeOpen = (leftEyeOpen + rightEyeOpen) / 2f
//
//    return when {
//        smilingProb > 0.6f -> "😊 Happy"
//        smilingProb < 0.2f && avgEyeOpen > 0.7f -> "😢 Sad"
//        avgEyeOpen > 0.95f && smilingProb > 0.3f && smilingProb < 0.6f -> "😲 Surprised"
//        smilingProb < 0.15f && avgEyeOpen > 0.5f && avgEyeOpen < 0.8f -> "😠 Angry"
//        avgEyeOpen < 0.3f -> "😴 Sleepy"
//        else -> "😐 Neutral"
//    }
//}
//
//

package com.example.detect

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.objects.DetectedObject
import android.util.Log

@Composable
fun DetectionOverlay(
    faces: List<Face> = emptyList(),
    objects: List<DetectedObject> = emptyList(),
    modifier: Modifier = Modifier
) {
    println("🔥 OVERLAY - Faces: ${faces.size}, Objects: ${objects.size}")
    Log.d("DetectionOverlay", "🔥 OVERLAY - Faces: ${faces.size}, Objects: ${objects.size}")

    val density = LocalDensity.current
    val strokeWidth = with(density) { 3.dp.toPx() }
    val canvasTextSize = with(density) { 16.dp.toPx() }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // FACE DETECTION - Green boxes
        faces.forEachIndexed { index, face ->
            val boundingBox = face.boundingBox
            val scaleX = canvasWidth / 640f
            val scaleY = canvasHeight / 480f

            val faceLeft = maxOf(0f, boundingBox.left.toFloat() * scaleX)
            val faceTop = maxOf(0f, boundingBox.top.toFloat() * scaleY)
            val faceRight = minOf(canvasWidth, boundingBox.right.toFloat() * scaleX)
            val faceBottom = minOf(canvasHeight, boundingBox.bottom.toFloat() * scaleY)

            val boxWidth = faceRight - faceLeft
            val boxHeight = faceBottom - faceTop

            if (boxWidth > 5 && boxHeight > 5) {
                val emotion = analyzeEmotionRealTime(face)
                val boxColor = Color.Green

                drawRect(
                    color = boxColor,
                    topLeft = androidx.compose.ui.geometry.Offset(faceLeft, faceTop),
                    size = androidx.compose.ui.geometry.Size(boxWidth, boxHeight),
                    style = Stroke(width = strokeWidth)
                )

                val label = "Face: $emotion"

                drawRect(
                    color = boxColor.copy(alpha = 0.8f),
                    topLeft = androidx.compose.ui.geometry.Offset(faceLeft, maxOf(faceTop - canvasTextSize - 5, 0f)),
                    size = androidx.compose.ui.geometry.Size(label.length * canvasTextSize * 0.6f, canvasTextSize + 10)
                )

                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    faceLeft,
                    maxOf(faceTop - 10, canvasTextSize),
                    android.graphics.Paint().apply {
                        color = Color.White.toArgb()
                        textSize = canvasTextSize
                        isAntiAlias = true
                        isFakeBoldText = true
                    }
                )
            }
        }

        // OBJECT DETECTION - Different colors + Better alignment
        objects.forEachIndexed { index, obj ->
            val boundingBox = obj.boundingBox

            // FIXED SCALING for objects
            val scaleX = canvasWidth / 1080f
            val scaleY = canvasHeight / 1920f

            val objLeft = maxOf(0f, boundingBox.left.toFloat() * scaleX)
            val objTop = maxOf(0f, boundingBox.top.toFloat() * scaleY)
            val objRight = minOf(canvasWidth, boundingBox.right.toFloat() * scaleX)
            val objBottom = minOf(canvasHeight, boundingBox.bottom.toFloat() * scaleY)

            val boxWidth = objRight - objLeft
            val boxHeight = objBottom - objTop

            if (boxWidth > 10 && boxHeight > 10) {
                val bestLabel = obj.labels.maxByOrNull { it.confidence }
                val objectName = bestLabel?.text ?: "Object"
                val confidence = ((bestLabel?.confidence ?: 0f) * 100).toInt()

                // DIFFERENT COLORS based on object type
                val boxColor = when {
                    objectName.contains("food", true) -> Color.Red
                    objectName.contains("person", true) -> Color.Yellow
                    objectName.contains("phone", true) -> Color.Cyan
                    objectName.contains("bottle", true) -> Color.Magenta
                    else -> Color.Blue
                }

                println("🔥 Object: $objectName at (${objLeft}, ${objTop}) size ${boxWidth}x${boxHeight}")

                // Draw object box
                drawRect(
                    color = boxColor,
                    topLeft = androidx.compose.ui.geometry.Offset(objLeft, objTop),
                    size = androidx.compose.ui.geometry.Size(boxWidth, boxHeight),
                    style = Stroke(width = strokeWidth)
                )

                // Enhanced label with confidence
                val label = "$objectName ($confidence%)"

                // Background for label
                drawRect(
                    color = boxColor.copy(alpha = 0.8f),
                    topLeft = androidx.compose.ui.geometry.Offset(objLeft, maxOf(objTop - canvasTextSize - 5, 0f)),
                    size = androidx.compose.ui.geometry.Size(label.length * canvasTextSize * 0.5f, canvasTextSize + 10)
                )

                // Label text
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    objLeft,
                    maxOf(objTop - 10, canvasTextSize),
                    android.graphics.Paint().apply {
                        color = Color.White.toArgb()
                        textSize = canvasTextSize
                        isAntiAlias = true
                        isFakeBoldText = true
                    }
                )
            }
        }
    }
}

private fun analyzeEmotionRealTime(face: Face): String {
    val smilingProb = face.smilingProbability ?: 0f
    val leftEyeOpen = face.leftEyeOpenProbability ?: 1f
    val rightEyeOpen = face.rightEyeOpenProbability ?: 1f
    val avgEyeOpen = (leftEyeOpen + rightEyeOpen) / 2f

    return when {
        smilingProb > 0.6f -> "😊 Happy"
        smilingProb < 0.2f && avgEyeOpen > 0.7f -> "😢 Sad"
        avgEyeOpen > 0.95f && smilingProb > 0.3f && smilingProb < 0.6f -> "😲 Surprised"
        smilingProb < 0.15f && avgEyeOpen > 0.5f && avgEyeOpen < 0.8f -> "😠 Angry"
        avgEyeOpen < 0.3f -> "😴 Sleepy"
        else -> "😐 Neutral"
    }
}

