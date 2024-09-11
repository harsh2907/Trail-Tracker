package com.example.trailtracker.onboarding

import com.example.trailtracker.mainScreen.domain.models.User

data class AuthState(
    val isSignInSuccessful: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false,
    val isNewUser: Boolean = true,
    val user: User? = null
)