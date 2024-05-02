package com.github.se.assocify.ui.screens.event.maptab

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory.DEFAULT_TILE_SOURCE
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay

// Initial position and zoom of the map
private val INITIAL_POSITION = GeoPoint(46.518726, 6.566613)
private const val INITIAL_ZOOM = 15.0

/** A screen that displays a map of the event: location with the associated tasks. */
@Composable
fun EventMapScreen() {
  Column(
      modifier =
          Modifier.fillMaxWidth()
              // TODO : padding is used to avoid a bug where the map
              //        overlaps the top bar. Has to be changed later on.
              .padding(top = 80.dp)
              .testTag("OSMMapScreen")) {
        EventMapView()
      }
}

@Composable
fun EventMapView() {
  AndroidView(
      factory = { context ->
        val mapView = createMapView(context)
        mapView
      },
      modifier = Modifier.testTag("EPFLMapView").fillMaxWidth(),
      update = { view ->
        // TODO : viewmodel stuff will be here
      })
}

/**
 * Creates a new MapView with the default configuration
 *
 * @param context the context of the application
 */
private fun createMapView(context: Context): MapView {
  // For some reason, required for OSMdroid
  Configuration.getInstance().userAgentValue = context.packageName
  return initMapView(context)
}

/**
 * Initializes the MapView with the desired configuration
 *
 * @param context the context of the application
 */
private fun initMapView(context: Context): MapView {
  val mapView = MapView(context)
  mapView.setTileSource(DEFAULT_TILE_SOURCE)
  // Zoom buttons only appears on touch and then fade out
  mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
  // Enable pinch to zoom
  mapView.setMultiTouchControls(true)
  // Initial settings
  mapView.controller.setZoom(INITIAL_ZOOM)
  mapView.controller.setCenter(INITIAL_POSITION)
  // Sets the tile source ot the EPFL plan tiles
  val campusTileSource = CampusTileSource(0)
  val tileProvider = MapTileProviderBasic(context, campusTileSource)
  val tilesOverlay = TilesOverlay(tileProvider, context)
  mapView.overlays.add(tilesOverlay)

  return mapView
}

/** The custom tile source from the EPFL plan API.
 * @param floorId the floor id of the map to display
 * */
class CampusTileSource(private val floorId: Int) :
    OnlineTileSourceBase("EPFLCampusTileSource", 0, 18, 256, ".png", arrayOf()) {
  override fun getTileURLString(pMapTileIndex: Long): String {
    // EPFL plan API has 3 servers, tilesX correspond to the server number
    return "https://plan-epfl-tiles3.epfl.ch/1.0.0/batiments/default/20160712/$floorId/3857/${
      MapTileIndex.getZoom(
        pMapTileIndex
      )
    }/${MapTileIndex.getY(pMapTileIndex)}/${MapTileIndex.getX(pMapTileIndex)}.png"
  }
}
