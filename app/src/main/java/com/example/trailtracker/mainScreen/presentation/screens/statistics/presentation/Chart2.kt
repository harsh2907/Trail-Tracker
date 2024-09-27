package com.example.trailtracker.mainScreen.presentation.screens.statistics.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.data.rememberExtraLambda
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.component.TextComponent
import java.util.Arrays.fill

@Composable
fun DemoChart(modelProducer: CartesianChartModelProducer, modifier: Modifier) {
  val marker = rememberDefaultCartesianMarker(label = TextComponent())
  CartesianChartHost(
    chart =
    rememberCartesianChart(
      rememberLineCartesianLayer(
        LineCartesianLayer.LineProvider.series(
          LineCartesianLayer.rememberLine(
            remember { LineCartesianLayer.LineFill.single(fill(Color(0xffa485e0))) }
          )
        )
      ),
      startAxis = VerticalAxis.rememberStart(),
      bottomAxis =
      HorizontalAxis.rememberBottom(
        guideline = null,
        itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() },
      ),
      marker = marker,
      layerPadding =
      cartesianLayerPadding(scalableStartPadding = 16.dp, scalableEndPadding = 16.dp),
      persistentMarkers = rememberExtraLambda(marker) { marker at PERSISTENT_MARKER_X },
    ),
    modelProducer = modelProducer,
    modifier = modifier,
    zoomState = rememberVicoZoomState(zoomEnabled = false),
  )
}

private const val PERSISTENT_MARKER_X = 7f

private val x = (1..50).toList()