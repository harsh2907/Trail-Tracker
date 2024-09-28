package com.example.trailtracker.mainScreen.presentation.screens.statistics.presentation

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.core.axis.AxisRenderer
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.legend.LegendItem

@Composable
fun DemoChart(
    modifier: Modifier = Modifier,
    modelProducer: ChartEntryModelProducer,
    chart: LineChart
) {

    Chart(
        modifier = modifier.background(Color.White),
        chart = chart,
        chartModelProducer = modelProducer,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(),
    )

}

private const val PERSISTENT_MARKER_X = 7f

private val x = (1..50).toList()