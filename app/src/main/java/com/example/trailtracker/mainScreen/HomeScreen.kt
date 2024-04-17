package com.example.trailtracker.mainScreen

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.navigation.compose.rememberNavController
import com.example.trailtracker.mainScreen.screens.MainScreenNavigation
import com.example.trailtracker.ui.theme.UiColors

@Composable
fun MainScreen() {
    val bottomNavController = rememberNavController()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {   }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add")
            }
        },
        bottomBar = {
            MainScreenBottomNavigationBar(navController = bottomNavController)
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
