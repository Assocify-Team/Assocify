package com.github.se.assocify.ui.screens.event.maptab

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.MapMarkerData
import com.github.se.assocify.ui.composables.CenteredCircularIndicator
import com.github.se.assocify.ui.composables.ErrorMessage
import com.github.se.assocify.ui.screens.event.EventScreenViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK
import org.osmdroid.tileprovider.tilesource.TileSourceFactory.WIKIMEDIA
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay
import java.io.File
import kotlin.random.Random

// Initial position and zoom of the map
private val INITIAL_POSITION = GeoPoint(46.518726, 6.566613)
private const val INITIAL_ZOOM = 17.0


/** A screen that displays a map of the event: location with the associated tasks. */
@Composable
fun EventMapScreen(viewModel: EventScreenViewModel) {
  val state by viewModel.mapViewModel.uiState.collectAsState()

  if (state.loading) {
    CenteredCircularIndicator()
    return
  }

  if (state.error != null) {
    ErrorMessage(errorMessage = state.error) { viewModel.mapViewModel.fetchTasks() }
    return
  }


  Column(modifier = Modifier
    .fillMaxWidth()
    .testTag("OSMMapScreen")) {
    // EventMapView()
    EPFLMapView(modifier = Modifier.fillMaxWidth(), { }, viewModel, state.markers)
  }
}

/**
 * Initialize the map view with the EPFL plan tiles and it's lifecycle in order to save its state
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun rememberMapViewWithLifecycle(viewModel: EventScreenViewModel): MapView {
  val context = LocalContext.current

  // Update OSM configuration, for some reason
  Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
  Configuration.getInstance().tileFileSystemCacheMaxBytes = 50L * 1024 * 1024
  Configuration.getInstance().osmdroidTileCache =
    File(context.cacheDir, "osmdroid").also { it.mkdir() }

  // Initialise the map view
  val mapView = remember {
    MapView(context).apply {
      setTileSource(MAPNIK)
      // Zoom buttons only appears on touch and then fade out
      zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
      // Enable pinch to zoom
      setMultiTouchControls(true)
      // Initial settings
      controller.setZoom(INITIAL_ZOOM)
      controller.setCenter(INITIAL_POSITION)
      // Sets the tile source ot the EPFL plan tiles
      val campusTileSource = CampusTileSource(0)
      val tileProvider = MapTileProviderBasic(context, campusTileSource)
      val tilesOverlay = TilesOverlay(tileProvider, context)
      overlays.add(tilesOverlay)
      clipToOutline = true
    }
  }

  // Make the mapview live as long as the composable
  val lifecycleObserver = rememberLifecycleObserver(mapView)
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  DisposableEffect(lifecycle) {
    lifecycle.addObserver(lifecycleObserver)
    onDispose { lifecycle.removeObserver(lifecycleObserver) }
  }

  //loadMapOverlay(mapView, viewModel.mapViewModel)

  return mapView
}

/**
 * Observer in order to manage events of the map Useful to save the map, its components and its
 * state
 *
 * @param mapView the map view to observe
 */
@Composable
fun rememberLifecycleObserver(mapView: MapView): LifecycleObserver =
    remember(mapView) {
      LifecycleEventObserver { _, event ->
        when (event) {
          Lifecycle.Event.ON_RESUME -> mapView.onResume()
          Lifecycle.Event.ON_PAUSE -> mapView.onPause()
          else -> {}
        }
      }
    }

val markersList = mutableListOf<Marker>()

/** A composable that displays a map view. */
@Composable
fun EPFLMapView(modifier: Modifier,
                onLoad: ((map: MapView) -> Unit)? = null,
                viewModel: EventScreenViewModel,
                markers: List<MapMarkerData>) {
  val mapViewState = rememberMapViewWithLifecycle(viewModel)

  // DEBUG
  markersList.forEach { marker -> mapViewState.overlays.remove(marker) }

  markers.forEach { markerData ->
    val marker = Marker(mapViewState)
    marker.title = markerData.name
    marker.snippet = markerData.description
    marker.position = markerData.position

    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    mapViewState.overlays.add(marker)

    markersList.add(marker)
  }

  AndroidView(
      factory = { mapViewState }, modifier = modifier, update = { view -> onLoad?.invoke(view) })
}

/**
 * The custom tile source from the EPFL plan API.
 *
 * @param floorId the floor id of the map to display
 */
class CampusTileSource(private val floorId: Int) :
    OnlineTileSourceBase("EPFLCampusTileSource", 0, 18, 256, ".png", arrayOf()) {
  override fun getTileURLString(pMapTileIndex: Long): String {
    // Select at random the map server to use
    val epflCampusServerCount = 3
    // EPFL plan API has 3 servers, tilesX correspond to the server number
    return "https://plan-epfl-tiles${Random.nextInt(epflCampusServerCount)}.epfl.ch/1.0.0/batiments/default/20160712/$floorId/3857/${
      MapTileIndex.getZoom(
        pMapTileIndex
      )
    }/${MapTileIndex.getY(pMapTileIndex)}/${MapTileIndex.getX(pMapTileIndex)}.png"
  }
}
