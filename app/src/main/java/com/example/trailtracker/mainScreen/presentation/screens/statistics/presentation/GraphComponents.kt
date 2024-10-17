package com.example.trailtracker.mainScreen.presentation.screens.statistics.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.trailtracker.mainScreen.presentation.screens.statistics.utils.GraphType
import com.example.trailtracker.ui.theme.UiColors
import java.time.LocalDate

@Composable
fun GraphSelectionMenu(
    isGraphSelectionVisible: Boolean,
    onDismissRequest: () -> Unit,
    onGraphTypeSelected: (GraphType) -> Unit,
    onIconClick: () -> Unit
) {
    IconButton(onClick = onIconClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Sort,
            contentDescription = "graph type"
        )
    }

    DropdownMenu(
        expanded = isGraphSelectionVisible,
        onDismissRequest = onDismissRequest
    ) {
        GraphType.entries.forEach { type ->
            DropdownMenuItem(
                text = { Text(text = type.name.lowercase()) },
                onClick = { onGraphTypeSelected(type) }
            )
        }
    }
}

@Composable
fun RenderGraph(
    modifier: Modifier = Modifier,
    selectedGraphType: GraphType,
    todayData: Map<Long, Float>,
    weeklyData: Map<LocalDate, Float>,
    overallData: Map<LocalDate, Float>,
    paddingValues: PaddingValues
) {
    when (selectedGraphType) {
        GraphType.DAILY -> {
            RenderGraphContent(
                isDataSetEmpty = todayData.isEmpty(),
                paddingValues = paddingValues,
                chartContent = {
                    TodayStatisticsChart(
                        modifier = modifier,
                        sessionDataPoints = todayData
                    )
                }
            )
        }

        GraphType.WEEKLY -> {
            RenderGraphContent(
                isDataSetEmpty = weeklyData.isEmpty(),
                paddingValues = paddingValues,
                chartContent = {
                    WeeklyStatisticsChart(
                        modifier = modifier,
                        weeklyDataPoints = weeklyData
                    )
                }
            )
        }

        GraphType.OVERALL -> {
            RenderGraphContent(
                isDataSetEmpty = overallData.isEmpty(),
                paddingValues = paddingValues,
                chartContent = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        OverallStatisticsChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(UiColors.EerieBlack) ,
                            overallDataPoints = overallData
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun RenderGraphContent(
    isDataSetEmpty: Boolean,
    paddingValues: PaddingValues,
    chartContent: @Composable () -> Unit
) {
    if (isDataSetEmpty) {
        EmptyGraphMessage(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    } else {
        chartContent()
    }
}

@Composable
fun EmptyGraphMessage(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(text = "No Data to plot...")
    }
}