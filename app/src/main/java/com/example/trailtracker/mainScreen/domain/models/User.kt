package com.example.trailtracker.mainScreen.domain.models

data class User(
    val userId:String = "",
    val username:String = "",
    val email:String = "",
    val profileImageUrl:String = "",
    val joinedAt:Long = 0L
)