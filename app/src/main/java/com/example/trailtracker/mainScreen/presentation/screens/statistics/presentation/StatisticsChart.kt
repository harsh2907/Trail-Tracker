package com.example.trailtracker.mainScreen.presentation.screens.statistics.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trailtracker.ui.theme.UiColors
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun OverallStatisticsChart(
    modifier: Modifier = Modifier,
    overallDataPoints: Map<LocalDate, Float>
) {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")
    val xToDateMapKey = ExtraStore.Key<Map<Double, LocalDate>>()
    val modelProducer = remember { CartesianChartModelProducer() }
    val xToDates = remember { overallDataPoints.keys.associateBy { it.toEpochDay().toDouble() } }

    LaunchedEffect(overallDataPoints) {
        modelProducer.runTransaction {
            lineSeries { series(xToDates.keys, overallDataPoints.values.map { it / 60.0 }) }
            extras { it[xToDateMapKey] = xToDates }
        }
    }

    val labelValueFormatter = remember {
        CartesianValueFormatter { context, x: Double, _ ->
            val date =
                context.model.extraStore[xToDateMapKey][x] ?: LocalDate.ofEpochDay(x.toLong())
            date.format(dateTimeFormatter)
        }
    }


    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(Fill(UiColors.secondaryColor.toArgb()))
                    )
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                title = "Duration (mins)",
                titleComponent = rememberTextComponent(
                    color = Color.White,
                    textSize = 16.sp,
                    padding = Dimensions(6f)
                ),
                label = rememberTextComponent(color = Color.White),
                itemPlacer = remember { VerticalAxis.ItemPlacer.step(step = { 0.5 }) }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = rememberTextComponent(
                    color = Color.White,
                    padding = Dimensions(horizontalDp = 4f)
                ),
                valueFormatter = labelValueFormatter
            ),
            marker = rememberDefaultCartesianMarker(label = rememberTextComponent())
        ),
        zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(),
        runInitialAnimation = true,
        modifier = modifier
    )

}

@Composable
fun WeeklyStatisticsChart(
    modifier: Modifier = Modifier,
    weeklyDataPoints: Map<LocalDate, Float>
) {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")
    val xToDateMapKey = ExtraStore.Key<Map<Double, LocalDate>>()
    val modelProducer = remember { CartesianChartModelProducer() }
    val xToDates = remember { weeklyDataPoints.keys.associateBy { it.toEpochDay().toDouble() } }

    LaunchedEffect(weeklyDataPoints) {
        modelProducer.runTransaction {
            columnSeries { series(xToDates.keys, weeklyDataPoints.values.map { it / 60.0 }) }
            extras { it[xToDateMapKey] = xToDates }
        }
    }

    val labelValueFormatter = remember {
        CartesianValueFormatter { context, x: Double, _ ->
            val date =
                context.model.extraStore[xToDateMapKey][x] ?: LocalDate.ofEpochDay(x.toLong())
            date.format(dateTimeFormatter)
        }
    }


    CartesianChartHost(
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        color = UiColors.secondaryColor,
                        thickness = 16.dp
                    )
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                title = "Duration (mins)",
                titleComponent = rememberTextComponent(
                    color = Color.White,
                    textSize = 16.sp,
                    padding = Dimensions(6f)
                ),
                label = rememberTextComponent(color = Color.White),
                itemPlacer = remember { VerticalAxis.ItemPlacer.step(step = { 0.5 }) }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = rememberTextComponent(
                    color = Color.White,
                    padding = Dimensions(horizontalDp = 4f)
                ),
                valueFormatter = labelValueFormatter
            ),
            marker = rememberDefaultCartesianMarker(label = rememberTextComponent())
        ),
        zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(),
        runInitialAnimation = true,
        modifier = modifier
    )

}


@Composable
fun TodayStatisticsChart(
    modifier: Modifier = Modifier,
    sessionDataPoints: Map<Long, Float>
) {
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a") // For formatting hours and minutes
    val xToTimeMapKey = ExtraStore.Key<Map<Double, Long>>() // Key for storing time mappings

    val modelProducer = remember { CartesianChartModelProducer() }

    // Map the epoch time to hours (as Double)
    val xToTimes = remember(sessionDataPoints) {
        sessionDataPoints.keys.associateBy { epochTime ->
            val localDateTime = Instant.ofEpochMilli(epochTime)
                .atZone(ZoneId.systemDefault())
                .toLocalTime() // Extract LocalTime

            localDateTime.hour + localDateTime.minute / 60.0 // Convert to fractional hours

        }
    }

    LaunchedEffect(sessionDataPoints) {
        modelProducer.runTransaction {
            columnSeries { series(xToTimes.keys, sessionDataPoints.values.map { it / 60.0 }) }
            extras { it[xToTimeMapKey] = xToTimes }
        }
    }

    // Formatter for displaying the hour on the X axis
    val labelValueFormatter = remember {
        CartesianValueFormatter { context, x: Double, _ ->
            val epochTime = context.model.extraStore.getOrNull(xToTimeMapKey)?.get(x)
            val localTime = epochTime?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime()
            } ?: LocalTime.ofSecondOfDay((x * 3600).toLong()) // Handle fallback

            localTime.format(timeFormatter).also { Log.e("Label Value Formatter",epochTime.toString()) }
        }
    }


    CartesianChartHost(
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        color = UiColors.secondaryColor,
                        thickness = 16.dp
                    )
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                title = "Duration (mins)",
                titleComponent = rememberTextComponent(
                    color = Color.White,
                    textSize = 16.sp,
                    padding = Dimensions(6f)
                ),
                label = rememberTextComponent(color = Color.White),
                itemPlacer = remember { VerticalAxis.ItemPlacer.step(step = { 0.5 }) }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = rememberTextComponent(
                    color = Color.White,
                    padding = Dimensions(horizontalDp = 4f)
                ),
                valueFormatter = labelValueFormatter
            ),
            marker = rememberDefaultCartesianMarker(label = rememberTextComponent())
        ),
        zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(),
        runInitialAnimation = true,
        modifier = modifier
    )


}

