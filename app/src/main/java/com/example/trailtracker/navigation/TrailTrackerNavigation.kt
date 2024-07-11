package com.example.trailtracker.navigation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.trailtracker.mainScreen.presentation.MainScreen
import com.example.trailtracker.onboarding.presentation.OnBoardingScreen
import com.example.trailtracker.onboardingDetails.presentation.OnBoardingViewModel
import com.example.trailtracker.onboardingDetails.presentation.OnboardingDetailsScreen

@Composable
fun TrailTrackerNavigation(
    navController: NavHostController,
    startDestination: String,
    shouldNavigateToSession: Boolean
) {

    NavHost(navController = navController, startDestination = startDestination) {

        composable(route = Screens.IdleScreen.route){
            Box(
                modifier = Modifier
                .fillMaxSize()
                .background(Color.Black))
        }

        composable(route = Screens.OnBoardingScreen.route) {
            OnBoardingScreen(
                onClick = {
                    navController.popBackStack()
                    navController.navigate(Screens.OnBoardingDetailsScreen.route)
                }
            )
        }

        composable(route = Screens.OnBoardingDetailsScreen.route) {
            val context = LocalContext.current
            val onBoardingViewModel: OnBoardingViewModel = hiltViewModel()

            OnboardingDetailsScreen(
                onContinue = { name, weight ->
                    if(weight.toInt()<150){
                        onBoardingViewModel.setDetails(name,weight.toInt())
                        navController.popBackStack()
                        navController.navigate(Screens.MainScreen.route)

                    } else{
                        Toast.makeText(context, "Please enter a valid weight", Toast.LENGTH_SHORT).show()
                    }
                }
            )

        }

        composable(route = Screens.MainScreen.route) {
            MainScreen(
                shouldNavigateToSession = shouldNavigateToSession
            )
        }
    }
}