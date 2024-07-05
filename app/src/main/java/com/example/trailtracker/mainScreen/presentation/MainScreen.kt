package com.example.trailtracker.mainScreen.presentation

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trailtracker.mainScreen.presentation.screens.MainScreenNavigation
import com.example.trailtracker.ui.theme.UiColors

@Composable
fun MainScreen() {
    val bottomNavController = rememberNavController()
    val backstack by bottomNavController.currentBackStackEntryAsState()

    val showBottomBar = remember(backstack?.destination?.route) {
        backstack?.destination?.route != Destinations.Home.Run.route
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar
            ) {
                MainScreenBottomNavigationBar(navController = bottomNavController)
            }
        }
    ) { paddingValues ->
        val window = (LocalView.current.context as Activity).window
        window.navigationBarColor = UiColors.EerieBlack.toArgb()

        MainScreenNavigation(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            navController = bottomNavController
        )
    }
}
