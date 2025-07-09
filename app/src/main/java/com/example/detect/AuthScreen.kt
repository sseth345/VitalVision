package com.example.detect

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    isDarkMode: Boolean = true
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authManager = remember { GoogleAuthManager(context) }

    // Colors
    val prussianBlue = Color(0xFF003153)
    val cyanBg = Color(0xFF00E5FF)
    val whiteText = Color.White
    val lightBlue = Color(0xFF63B3ED)

    // Google Sign In Launcher
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        scope.launch {
            val success = authManager.handleSignInResult(result.data)
            if (success) {
                onAuthSuccess()
            }
        }
    }

    // Check if already authenticated
    LaunchedEffect(authManager.isAuthenticated.value) {
        if (authManager.isAuthenticated.value) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(prussianBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Stethoscope Icon
            Text(
                text = "🩺",
                fontSize = 80.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // App Name
            Text(
                text = "VitalVision",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = whiteText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "Emotion monitoring with Object analysis",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = whiteText.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Sign In Text
            Text(
                text = "Sign In to continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = whiteText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Continue with Google Button - Cyan BG with White Text
            Button(
                onClick = {
                    val signInIntent = authManager.getSignInIntent()
                    signInLauncher.launch(signInIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !authManager.isLoading.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = cyanBg
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (authManager.isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Signing in...",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message
            authManager.authError.value?.let { error ->
                Text(
                    text = "❌ $error",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }

        // Created by Siddharth Seth - Bottom
        Text(
            text = "Created by Siddharth Seth",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            fontSize = 12.sp,
            color = lightBlue,
            textAlign = TextAlign.Center
        )
    }
}
