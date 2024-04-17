package com.example.trailtracker.mainScreen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.trailtracker.R
import com.example.trailtracker.ui.theme.UiColors

sealed class Destinations(
    val name: String,
    @DrawableRes val icon: Int,
    val route: String
) {
    data object Home : Destinations("Home", R.drawable.ic_home, "Home")
    data object Statistics : Destinations("Statistics", R.drawable.ic_stats, "Statistics")
    data object Profile : Destinations("Profile", R.drawable.ic_person, "Profile")
}


@Composable
fun MainScreenBottomNavigationBar(
    navController: NavController
) {
    val backstack by navController.currentBackStackEntryAsState()
    val currentDestination = backstack?.destination?.route
    val screens = listOf(Destinations.Home, Destinations.Statistics, Destinations.Profile)

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                shape = ShapeDefaults.Medium.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0))
                clip = true
            },
        containerColor = UiColors.EerieBlack
    ) {
        screens.forEach { screen ->
            NavigationBarItem(
                selected = screen.route == currentDestination,
                onClick = { navController.navigate(screen.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.name
                    )
                },
                label = { Text(text = screen.name) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = UiColors.primaryColor,
                    selectedIconColor = UiColors.primaryColor,
                    indicatorColor = Color.Black.copy(alpha = 0.2f)
                )
            )
        }
    }
}