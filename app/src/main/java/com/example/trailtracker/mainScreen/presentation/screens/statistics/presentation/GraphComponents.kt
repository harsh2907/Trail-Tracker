package com.example.trailtracker.mainScreen.presentation.screens.statistics.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.trailtracker.mainScreen.presentation.screens.statistics.utils.GraphType
import com.example.trailtracker.ui.theme.UiColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
fun DailyStatisticsGraph(
    currentDate: LocalDate,
    paddingValues: PaddingValues,
    todayData: Map<Long, Float>,
    onDateForward: () -> Unit,
    onDateBackward: () -> Unit
) {

    val formattedLocalDate = remember(currentDate) {
        DateTimeFormatter.ofPattern("dd MMM yyyy").let { currentDate.format(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UiColors.EerieBlack)
            .padding(paddingValues)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = UiColors.EerieBlack
                ),
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth(0.5f),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDateBackward) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "back arrow",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = formattedLocalDate,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    IconButton(onClick = onDateForward) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "back arrow",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        RenderGraphContent(
            isDataSetEmpty = todayData.isEmpty(),
            paddingValues = paddingValues,
            chartContent = {
                TodayStatisticsChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(UiColors.EerieBlack)
                        .padding(12.dp),
                    sessionDataPoints = todayData
                )
            }
        )
    }
}

@Composable
fun RenderGraph(
    selectedGraphType: GraphType,
    todayData: Map<Long, Float>,
    weeklyData: Map<LocalDate, Float>,
    overallData: Map<LocalDate, Float>,
    paddingValues: PaddingValues,
    currentDate: LocalDate,
    onDateForward: () -> Unit,
    onDateBackward: () -> Unit
) {
    when (selectedGraphType) {
        GraphType.DAILY -> {
            DailyStatisticsGraph(
                paddingValues = paddingValues,
                todayData = todayData,
                currentDate = currentDate,
                onDateForward = onDateForward,
                onDateBackward = onDateBackward
            )
        }

        GraphType.WEEKLY -> {
            RenderGraphContent(
                isDataSetEmpty = weeklyData.isEmpty(),
                paddingValues = paddingValues,
                chartContent = {
                    WeeklyStatisticsChart(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(UiColors.EerieBlack)
                            .padding(12.dp),
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
                                .fillMaxSize()
                                .padding(paddingValues)
                                .background(UiColors.EerieBlack)
                                .padding(12.dp),
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