package com.example.trailtracker.mainScreen.screens.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NormalMapScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun NormalMapScreen(
    modifier: Modifier = Modifier
) {


    val cameraState = rememberCameraState(
        cameraProperty = {
            geoPoint = GeoPoint(29.8543, 77.8880)
            zoom = 15.0
        }
    )

    // define properties with remember with default value
    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }


    LaunchedEffect(Unit) {
        mapProperties = mapProperties
            .copy(isTilesScaledToDpi = true)
            .copy(tileSources = TileSourceFactory.MAPNIK)
            .copy(isEnableRotationGesture = true)
        //  .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
    }



    OpenStreetMap(
        modifier = modifier,
        cameraState = cameraState,
        properties = mapProperties
    )
}