package com.example.trailtracker.mainScreen.presentation.screens.statistics.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.trailtracker.ui.theme.UiColors
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DemoChart(
    modifier: Modifier = Modifier,
    overAllPoints: Map<LocalDate, Float>
) {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")
    val xToDateMapKey = ExtraStore.Key<Map<Double, LocalDate>>()
    val modelProducer = remember { CartesianChartModelProducer() }
    val xToDates = remember { overAllPoints.keys.associateBy { it.toEpochDay().toDouble() } }

    LaunchedEffect(overAllPoints) {
        modelProducer.runTransaction {
            columnSeries {
                //series(xToDates.keys, overAllPoints.values)
                series( overAllPoints.values)
            }
          //  extras { it[xToDateMapKey] = xToDates }
        }
    }

    val labelValueFormatter = remember {
        CartesianValueFormatter { context, x: Double, _ ->
            (context.model.extraStore[xToDateMapKey][x] ?: LocalDate.ofEpochDay(x.toLong()))
                .format(dateTimeFormatter)
        }
    }


    CartesianChartHost(
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        color = UiColors.primaryColor,
                        thickness = 8.dp
                    )
                ),
//                dataLabelValueFormatter = labelValueFormatter,
//                dataLabel = rememberTextComponent()
            ),
            startAxis = VerticalAxis.rememberStart(title = "Duration"),
            bottomAxis = HorizontalAxis.rememberBottom(title = "Session Date"),
        ),
        modelProducer = modelProducer,
        zoomState = rememberVicoZoomState(zoomEnabled = true),
        modifier = modifier
    )

}

private const val PERSISTENT_MARKER_X = 7f

private val x = (1..50).toList()