package com.example.trailtracker.navigation

sealed class Screens(val route:String) {
    data object IdleScreen:Screens("IdleScreen")
    data object OnBoardingScreen: Screens("OnBoardingScreen")
    data object OnBoardingDetailsScreen: Screens("OnBoardingDetailsScreen")
    data object MainScreen: Screens("MainScreen")
}