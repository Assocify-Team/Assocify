package com.github.se.assocify.ui.screens.event.maptab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

/** A screen that displays a map of the event location with the associated tasks. */
@Composable
fun EventMapScreen() {
  Column(modifier = Modifier.fillMaxWidth().testTag("MapScreen")) {
    GoogleMap(
        modifier = Modifier.testTag("GoogleMap"),
        cameraPositionState =
            rememberCameraPositionState {
              position = CameraPosition.fromLatLngZoom(LatLng(1.35, 103.87), 11f)
            }) {}
  }
}
