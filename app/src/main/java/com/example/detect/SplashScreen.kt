package com.example.detect

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val density = LocalDensity.current

    // Colors
    val prussianBlue = Color(0xFF003153)
    val heartRed = Color(0xFFE53E3E)
    val whiteText = Color.White
    val lightBlue = Color(0xFF63B3ED)
    val cyanCaption = Color(0xFF00E5FF)
    val heartLineColor = Color(0xFF00FF41)

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "heart_beat")

    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4000
                1f at 0
                1.4f at 200
                1f at 400
                1.4f at 600
                1f at 800
                1f at 4000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "heart_scale"
    )

    val heartLineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "heartbeat_line"
    )

    LaunchedEffect(Unit) {
        delay(6000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(prussianBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Beating Red Heart
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Heart",
                modifier = Modifier
                    .size(100.dp)
                    .scale(heartScale),
                tint = heartRed
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Heartbeat Line (BELOW Heart now)
            Canvas(
                modifier = Modifier
                    .width(200.dp)
                    .height(60.dp)
            ) {
                drawHeartbeatLine(
                    drawScope = this,
                    progress = heartLineProgress,
                    color = heartLineColor,
                    heartScale = heartScale,
                    density = density
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // VitalVision - White Bold Text
            Text(
                text = "VitalVision",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = whiteText,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Caption - Cyan Color BOLD
            Text(
                text = "Your one-stop solution to emotional health using AI",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold, // Made BOLD
                color = cyanCaption,
                textAlign = TextAlign.Center
            )
        }

        // Created by Siddharth Seth - Moved UP slightly
        Text(
            text = "Created by Siddharth Seth",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp), // Increased from 32dp to move UP
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = lightBlue,
            textAlign = TextAlign.Center
        )
    }
}

fun drawHeartbeatLine(
    drawScope: DrawScope,
    progress: Float,
    color: Color,
    heartScale: Float,
    density: androidx.compose.ui.unit.Density
) {
    val width = drawScope.size.width
    val height = drawScope.size.height
    val centerY = height / 2

    val path = Path()
    val lineWidth = width * progress
    val points = mutableListOf<Offset>()

    for (i in 0..lineWidth.toInt()) {
        val x = i.toFloat()
        var y = centerY

        val beatPosition1 = width * 0.2f
        val beatPosition2 = width * 0.4f

        when {
            x in (beatPosition1 - 20)..(beatPosition1 + 20) -> {
                val spike = sin((x - beatPosition1 + 20) * 0.3f) * 30f * heartScale
                y = centerY - spike
            }
            x in (beatPosition2 - 20)..(beatPosition2 + 20) -> {
                val spike = sin((x - beatPosition2 + 20) * 0.3f) * 25f * heartScale
                y = centerY - spike
            }
            else -> {
                y = centerY + sin(x * 0.02f) * 2f
            }
        }
        points.add(Offset(x, y))
    }

    if (points.size > 1) {
        path.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            path.lineTo(points[i].x, points[i].y)
        }

        drawScope.drawPath(
            path = path,
            color = color,
            style = Stroke(width = with(density) { 3.dp.toPx() })
        )
    }

    if (points.isNotEmpty()) {
        val lastPoint = points.last()
        drawScope.drawCircle(
            color = color,
            radius = with(density) { 4.dp.toPx() },
            center = lastPoint
        )
    }
}
