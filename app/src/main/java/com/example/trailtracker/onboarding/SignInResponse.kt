package com.example.trailtracker.onboarding

import com.example.trailtracker.mainScreen.domain.models.User

data class SignInResponse(
    val data: User? = null,
    val error: String? = null,
    val isNewUser:Boolean = true
)