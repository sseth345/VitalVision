package com.example.detect

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient

    // Auth state
    var isAuthenticated = mutableStateOf(false)
    var currentUser = mutableStateOf<FirebaseUser?>(null)
    var authError = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    init {
        // Configure Google Sign In with your Web Client ID
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("187858471383-8nlpsa67vl8318iocc1fb2bi9qqm1trt.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)

        // Check current auth state
        checkAuthState()

        // Listen to auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            isAuthenticated.value = user != null
            currentUser.value = user

            if (user != null) {
                saveUserSession(user)
                println("🔥 AUTH STATE CHANGED: User logged in - ${user.displayName}")
                Log.d("GoogleAuth", "🔥 AUTH STATE CHANGED: User logged in - ${user.displayName}")
            } else {
                clearUserSession()
                println("🔥 AUTH STATE CHANGED: User logged out")
                Log.d("GoogleAuth", "🔥 AUTH STATE CHANGED: User logged out")
            }
        }

        println("🔥 GOOGLE AUTH MANAGER INITIALIZED")
        Log.d("GoogleAuth", "🔥 GOOGLE AUTH MANAGER INITIALIZED")
    }

    private fun checkAuthState() {
        val user = auth.currentUser
        isAuthenticated.value = user != null
        currentUser.value = user

        println("🔥 INITIAL AUTH STATE: ${if (user != null) "AUTHENTICATED" else "NOT AUTHENTICATED"}")
        Log.d("GoogleAuth", "🔥 INITIAL AUTH STATE: ${if (user != null) "AUTHENTICATED" else "NOT AUTHENTICATED"}")

        if (user != null) {
            println("🔥 CURRENT USER: ${user.displayName} (${user.email})")
            Log.d("GoogleAuth", "🔥 CURRENT USER: ${user.displayName} (${user.email})")
        }
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    suspend fun handleSignInResult(data: Intent?): Boolean {
        return try {
            isLoading.value = true
            authError.value = null

            println("🔥 HANDLING SIGN IN RESULT")
            Log.d("GoogleAuth", "🔥 HANDLING SIGN IN RESULT")

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            println("🔥 GOOGLE ACCOUNT: ${account.displayName} (${account.email})")
            Log.d("GoogleAuth", "🔥 GOOGLE ACCOUNT: ${account.displayName} (${account.email})")

            firebaseAuthWithGoogle(account)

        } catch (e: ApiException) {
            println("❌ GOOGLE SIGN IN FAILED: ${e.message}")
            Log.e("GoogleAuth", "❌ GOOGLE SIGN IN FAILED", e)
            authError.value = "Google Sign In failed: ${e.message}"
            isLoading.value = false
            false
        }
    }

    private suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount): Boolean {
        return try {
            println("🔥 FIREBASE AUTH WITH GOOGLE")
            Log.d("GoogleAuth", "🔥 FIREBASE AUTH WITH GOOGLE")

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()

            val user = result.user
            if (user != null) {
                println("🔥 FIREBASE AUTH SUCCESS: ${user.displayName}")
                Log.d("GoogleAuth", "🔥 FIREBASE AUTH SUCCESS: ${user.displayName}")

                isAuthenticated.value = true
                currentUser.value = user
                isLoading.value = false

                // Save session
                saveUserSession(user)
                true
            } else {
                println("❌ FIREBASE AUTH FAILED: User is null")
                Log.e("GoogleAuth", "❌ FIREBASE AUTH FAILED: User is null")
                authError.value = "Authentication failed"
                isLoading.value = false
                false
            }

        } catch (e: Exception) {
            println("❌ FIREBASE AUTH ERROR: ${e.message}")
            Log.e("GoogleAuth", "❌ FIREBASE AUTH ERROR", e)
            authError.value = "Authentication error: ${e.message}"
            isLoading.value = false
            false
        }
    }

    private fun saveUserSession(user: FirebaseUser) {
        val prefs = context.getSharedPreferences("auth_session", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("user_id", user.uid)
            putString("user_name", user.displayName)
            putString("user_email", user.email)
            putString("user_photo", user.photoUrl?.toString())
            putLong("login_time", System.currentTimeMillis())
            putBoolean("is_logged_in", true)
            apply()
        }

        println("🔥 USER SESSION SAVED")
        Log.d("GoogleAuth", "🔥 USER SESSION SAVED")
    }

    fun signOut() {
        try {
            println("🔥 SIGNING OUT")
            Log.d("GoogleAuth", "🔥 SIGNING OUT")

            // Firebase sign out
            auth.signOut()

            // Google sign out
            googleSignInClient.signOut()

            // Clear session
            clearUserSession()

            // Update state
            isAuthenticated.value = false
            currentUser.value = null
            authError.value = null

            println("🔥 SIGN OUT COMPLETE")
            Log.d("GoogleAuth", "🔥 SIGN OUT COMPLETE")

        } catch (e: Exception) {
            println("❌ SIGN OUT ERROR: ${e.message}")
            Log.e("GoogleAuth", "❌ SIGN OUT ERROR", e)
        }
    }

    private fun clearUserSession() {
        val prefs = context.getSharedPreferences("auth_session", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        println("🔥 USER SESSION CLEARED")
        Log.d("GoogleAuth", "🔥 USER SESSION CLEARED")
    }

    fun getUserSession(): Map<String, String?> {
        val prefs = context.getSharedPreferences("auth_session", Context.MODE_PRIVATE)
        return mapOf(
            "user_id" to prefs.getString("user_id", null),
            "user_name" to prefs.getString("user_name", null),
            "user_email" to prefs.getString("user_email", null),
            "user_photo" to prefs.getString("user_photo", null)
        )
    }

    fun isUserLoggedIn(): Boolean {
        val prefs = context.getSharedPreferences("auth_session", Context.MODE_PRIVATE)
        return prefs.getBoolean("is_logged_in", false) && auth.currentUser != null
    }
}
