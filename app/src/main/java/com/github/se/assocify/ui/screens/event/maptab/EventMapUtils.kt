package com.github.se.assocify.ui.screens.event.maptab

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * Load the map overlay with the markers of the tasks
 */
fun loadMapOverlay(
  mapView: MapView,
  viewModel: EventMapViewModel
) {
  val markers = viewModel.uiState.value.markers
  markers.forEach { markerData ->
    val marker = Marker(mapView)
    marker.title = markerData.name
    marker.snippet = markerData.description
    marker.position = markerData.position

    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    mapView.overlays.add(marker)
  }
}

/**
 * Convert the GeoPoint version of the latitude & longitude
 * to a String format used in the database storage.
 * The inverted version is GeoPoint.fromDoubleString
 * @param location the GeoPoint version of the latitude & longitude
 */
fun geoPointToString(location: GeoPoint): String {
  return "${location.latitude},${location.longitude}"
}