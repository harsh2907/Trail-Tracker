package com.example.trailtracker.mainScreen.presentation.screens.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.trailtracker.R
import com.example.trailtracker.mainScreen.domain.models.Run
import com.example.trailtracker.ui.theme.UiColors
import com.example.trailtracker.utils.SortType

@Composable
fun HomeScreen(
    allRuns:List<Run>,
    sortType: SortType,
    navigateToSession: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = navigateToSession,
                containerColor = UiColors.primaryColor
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.running_person),
                    contentDescription = "run",
                    tint = Color.Black
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Home Screen")
        }
    }
}

