package com.example.trailtracker.mainScreen.presentation.screens.home.presentation

import com.example.trailtracker.mainScreen.domain.models.RunItem

data class AllSessionsState(
    val runSessions:List<RunItem> = emptyList(),
    val isLoading:Boolean = false,
    val error:String = ""
)