package com.example.detect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log

@Composable
fun LandingScreen(
    onModeSelected: (String) -> Unit,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    authManager: GoogleAuthManager
) {
    println("🔥 LANDING SCREEN - Dark Mode: $isDarkMode")
    Log.d("LandingScreen", "🔥 LANDING SCREEN - Dark Mode: $isDarkMode")

    // Theme Colors
    val backgroundColor = if (isDarkMode) {
        Color(0xFF003153) // Prussian Blue
    } else {
        Color.White // White BG for light theme
    }

    val textColor = if (isDarkMode) Color.White else Color.Black
    val cardColor = if (isDarkMode) Color(0xFF1E3A8A) else Color(0xFFE8F5E8) // Light green for light theme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Top Bar - Half centimeter down
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Name - Left Corner with Green Cross
                authManager.currentUser.value?.let { user ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✚", // Green Cross instead of hand wave
                            fontSize = 16.sp,
                            color = Color.Green
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${user.displayName?.split(" ")?.first() ?: "User"}",
                            color = textColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Right Side Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Theme Toggle Button - Fixed with Emojis
                    Button(
                        onClick = {
                            println("🔥 THEME TOGGLE CLICKED")
                            Log.d("LandingScreen", "🔥 THEME TOGGLE CLICKED")
                            onThemeToggle()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkMode) Color.Yellow.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.width(48.dp)
                    ) {
                        Text(
                            text = if (isDarkMode) "🌕" else "☀️", // Moon for dark, Sun for light
                            fontSize = 18.sp
                        )
                    }

                    // Logout Button with Light Green Background
                    Button(
                        onClick = {
                            println("🔥 LOGOUT CLICKED")
                            Log.d("LandingScreen", "🔥 LOGOUT CLICKED")
                            onModeSelected("logout")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF81C784) // Light Green
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Logout",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(56.dp))

            // App Name with Stethoscope
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stethoscope Logo
                Text(
                    text = "🩺",
                    fontSize = 36.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                // App Name
                Text(
                    text = "VitalVision",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quote Caption
            Text(
                text = "\"An 🍎 a day keeps 👨‍⚕️ away\"",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Select your mode
            Text(
                text = "Select your mode →",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Two Square Tiles Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Emotional Scan Tile - Human Face Icon
                MedicalModeCard(
                    title = "Emotional Scan",
                    caption = "Check patient wellness",
                    icon = "👤",
                    onClick = {
                        println("🔥 EMOTIONAL SCAN SELECTED")
                        Log.d("LandingScreen", "🔥 EMOTIONAL SCAN SELECTED")
                        onModeSelected("face")
                    },
                    cardColor = cardColor,
                    textColor = textColor,
                    modifier = Modifier.weight(1f)
                )

                // Object Detection Tile - Car Icon
                MedicalModeCard(
                    title = "Object Detection",
                    caption = "Realtime monitor objects",
                    icon = "🚗",
                    onClick = {
                        println("🔥 OBJECT DETECTION SELECTED")
                        Log.d("LandingScreen", "🔥 OBJECT DETECTION SELECTED")
                        onModeSelected("object")
                    },
                    cardColor = cardColor,
                    textColor = textColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Patient Images - Horizontal Tile with Green Cross
            Card(
                onClick = {
                    println("🔥 PATIENT IMAGES SELECTED")
                    Log.d("LandingScreen", "🔥 PATIENT IMAGES SELECTED")
                    onModeSelected("storage")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "✚",
                        fontSize = 24.sp,
                        color = Color.Green
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Patient Images",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }

        // Created by Siddharth Seth - Bottom
        Text(
            text = "Created by Siddharth Seth",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            fontSize = 12.sp,
            color = if (isDarkMode) Color(0xFF63B3ED) else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MedicalModeCard(
    title: String,
    caption: String,
    icon: String,
    onClick: () -> Unit,
    cardColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f), // Square shape
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Text(
                text = icon,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Caption
            Text(
                text = caption,
                fontSize = 12.sp,
                color = textColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
