package com.example.trailtracker.navigation

sealed class Screens(val route:String) {
    data object IdleScreen:Screens("IdleScreen")
    data object AuthScreen: Screens("AuthScreen")
    data object MainScreen: Screens("MainScreen")
}