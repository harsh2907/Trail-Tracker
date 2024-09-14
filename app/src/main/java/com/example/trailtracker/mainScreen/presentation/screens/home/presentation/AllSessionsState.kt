package com.example.trailtracker.mainScreen.presentation.screens.home.presentation

import com.example.trailtracker.mainScreen.domain.models.Run

data class AllSessionsState(
    val runSessions:List<Run> = emptyList(),
    val isLoading:Boolean = false,
    val error:String = ""
)