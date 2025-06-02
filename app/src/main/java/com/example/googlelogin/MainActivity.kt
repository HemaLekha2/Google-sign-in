package com.example.googlelogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.googlelogin.domine.GoogleAuthClient
import com.example.googlelogin.presentation.MainScreen
import com.example.googlelogin.presentation.UserData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val googleAuthClient = GoogleAuthClient(applicationContext)

        setContent {
            MaterialTheme {
                var isSignedIn by rememberSaveable { mutableStateOf(false) }
                var isLoading by rememberSaveable { mutableStateOf(false) }
                var userData by rememberSaveable { mutableStateOf<UserData?>(null) }

                // Initialize authentication state
                LaunchedEffect(Unit) {
                    isSignedIn = googleAuthClient.isSingedIn()
                    if (isSignedIn) {
                        // Get actual user data from Firebase
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        userData = UserData(
                            name = currentUser?.displayName,
                            email = currentUser?.email,
                            profilePicture = currentUser?.photoUrl?.toString()
                        )
                    }
                }

                MainScreen(
                    isSignedIn = isSignedIn,
                    isLoading = isLoading,
                    userData = userData,
                    onSignInClick = {
                        isLoading = true
                        lifecycleScope.launch {
                            val success = googleAuthClient.signIn(this@MainActivity)
                            if (success) {
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                userData = UserData(
                                    name = currentUser?.displayName,
                                    email = currentUser?.email,
                                    profilePicture = currentUser?.photoUrl?.toString()
                                )
                            }
                            isSignedIn = success
                            isLoading = false
                        }
                    },
                    onSignOutClick = {
                        isLoading = true
                        lifecycleScope.launch {
                            googleAuthClient.signOut()
                            isSignedIn = false
                            userData = null
                            isLoading = false
                        }
                    }
                )
            }
        }
    }
}