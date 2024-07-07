package com.example.trailtracker.mainScreen.presentation

import android.Manifest
import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trailtracker.mainScreen.presentation.screens.MainScreenNavigation
import com.example.trailtracker.ui.theme.UiColors
import com.example.trailtracker.utils.TrackingUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    val bottomNavController = rememberNavController()
    val backstack by bottomNavController.currentBackStackEntryAsState()

    val showBottomBar = remember(backstack?.destination?.route) {
        backstack?.destination?.route != Destinations.Home.Run.route
    }

    val context = LocalContext.current

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS,
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    val multiplePermissionState = rememberMultiplePermissionsState(permissions = permissions)

    LaunchedEffect(true) {
        if (!TrackingUtils.hasAllPermissions(context)) {
            multiplePermissionState.launchMultiplePermissionRequest()
        }
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
