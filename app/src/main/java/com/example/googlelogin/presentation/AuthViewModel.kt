package com.example.googlelogin.presentation

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    val isUserSignedIn: Boolean
        get() = FirebaseAuth.getInstance().currentUser != null
}