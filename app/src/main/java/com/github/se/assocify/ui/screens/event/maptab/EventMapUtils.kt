package com.github.se.assocify.ui.screens.event.maptab

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

/**
 * Load the map overlay with the markers of the tasks
 */
fun loadMapOverlay(
  mapView: MapView,
  viewModel: EventMapViewModel
) {
  val markers = viewModel
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