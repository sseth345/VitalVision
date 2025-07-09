//package com.example.detect
//
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import com.example.detect.ui.theme.DetectTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        println("🔥 MAIN ACTIVITY CREATED")
//        Log.d("MainActivity", "🔥 MAIN ACTIVITY CREATED")
//
//        enableEdgeToEdge()
//        setContent {
//            DetectTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    AppNavigation()
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun AppNavigation() {
//    var currentScreen by remember { mutableStateOf("splash") }
//    var selectedMode by remember { mutableStateOf("") }
//    var isDarkMode by remember { mutableStateOf(true) }
//
//    println("🔥 APP NAVIGATION - Current Screen: $currentScreen")
//    Log.d("AppNavigation", "🔥 Current Screen: $currentScreen")
//
//    when (currentScreen) {
//        "splash" -> {
//            SplashScreen { currentScreen = "landing" }
//        }
//        "landing" -> {
//            LandingScreen(
//                onModeSelected = { mode ->
//                    when (mode) {
//                        "storage" -> currentScreen = "storage"
//                        else -> {
//                            selectedMode = mode
//                            currentScreen = "camera"
//                        }
//                    }
//                },
//                isDarkMode = isDarkMode,
//                onThemeToggle = { isDarkMode = !isDarkMode }
//            )
//        }
//        "camera" -> {
//            CameraScreen(
//                detectionMode = selectedMode,
//                onBackPressed = { currentScreen = "landing" },
//                isDarkMode = isDarkMode
//            )
//        }
//        "storage" -> {
//            StorageScreen(
//                onBackPressed = { currentScreen = "landing" },
//                isDarkMode = isDarkMode
//            )
//        }
//    }
//}
package com.example.detect

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.detect.ui.theme.DetectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("🔥 MAIN ACTIVITY CREATED")
        Log.d("MainActivity", "🔥 MAIN ACTIVITY CREATED")

        enableEdgeToEdge()
        setContent {
            DetectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val authManager = remember { GoogleAuthManager(context) }

    var currentScreen by remember { mutableStateOf("splash") }
    var selectedMode by remember { mutableStateOf("") }
    var isDarkMode by remember { mutableStateOf(true) }

    println("🔥 APP NAVIGATION - Current Screen: $currentScreen")
    Log.d("AppNavigation", "🔥 Current Screen: $currentScreen")

    when (currentScreen) {
        "splash" -> {
            SplashScreen {
                // After splash, check auth state
                currentScreen = if (authManager.isUserLoggedIn()) {
                    println("🔥 USER ALREADY LOGGED IN - GOING TO LANDING")
                    "landing"
                } else {
                    println("🔥 USER NOT LOGGED IN - GOING TO AUTH")
                    "auth"
                }
            }
        }

        "auth" -> {
            AuthScreen(
                onAuthSuccess = {
                    println("🔥 AUTH SUCCESS - NAVIGATING TO LANDING")
                    currentScreen = "landing"
                },
                isDarkMode = isDarkMode
            )
        }

        "landing" -> {
            LandingScreen(
                onModeSelected = { mode ->
                    when (mode) {
                        "storage" -> currentScreen = "storage"
                        "logout" -> {
                            authManager.signOut()
                            currentScreen = "auth"
                        }
                        else -> {
                            selectedMode = mode
                            currentScreen = "camera"
                        }
                    }
                },
                isDarkMode = isDarkMode,
                onThemeToggle = { isDarkMode = !isDarkMode },
                authManager = authManager
            )
        }

        "camera" -> {
            CameraScreen(
                detectionMode = selectedMode,
                onBackPressed = { currentScreen = "landing" },
                isDarkMode = isDarkMode
            )
        }

        "storage" -> {
            StorageScreen(
                onBackPressed = { currentScreen = "landing" },
                isDarkMode = isDarkMode
            )
        }
    }
}

