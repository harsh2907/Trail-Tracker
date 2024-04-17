package com.example.trailtracker.mainScreen.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.trailtracker.mainScreen.Destinations
import com.example.trailtracker.mainScreen.screens.home.presentation.HomeScreen
import com.example.trailtracker.mainScreen.screens.profile.presentation.ProfileScreen
import com.example.trailtracker.mainScreen.screens.statistics.presentation.StatisticsScreen

@Composable
fun MainScreenNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Destinations.Home.route
    ){

        composable(route = Destinations.Home.route){
            HomeScreen()
        }

        composable(route = Destinations.Statistics.route){
            StatisticsScreen()
        }

        composable(route = Destinations.Profile.route){
            ProfileScreen()
        }
    }
}