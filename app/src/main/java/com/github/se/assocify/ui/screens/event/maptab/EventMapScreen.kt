package com.github.se.assocify.ui.screens.event.maptab

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.MapMarkerData
import java.io.File
import kotlin.random.Random
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

// Initial position of the map (EPFL Agora)
private val INITIAL_POSITION = GeoPoint(46.518726, 6.566613)
// Initial zoom of the map (Zoom made to be focused on the EPFL campus)
private const val INITIAL_ZOOM = 17.0
// The location overlay to display the user's location
private var myLocationOverlay: MyLocationNewOverlay? = null
// Permission code for accessing GPS location
const val LOCATION_PERMISSION_REQUEST_CODE = 1000

/** A screen that displays a map of the event: location with the associated tasks. */
@Composable
fun EventMapScreen(viewModel: EventMapViewModel) {
  val state by viewModel.uiState.collectAsState()

  Column(modifier = Modifier.fillMaxWidth().testTag("OSMMapScreen")) {
    EPFLMapView(modifier = Modifier.fillMaxWidth(), {}, state.markers)
  }
}

/**
 * Initialize the map view with the EPFL plan tiles and it's lifecycle in order to save its state
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun rememberMapViewWithLifecycle(): MapView {
  val context = LocalContext.current

  // Update OSM configuration, for some reason
  Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
  Configuration.getInstance().tileFileSystemCacheMaxBytes = 50L * 1024 * 1024

  // Cache initialization. Will need to add something for error case handling
  val cacheDir = File(context.cacheDir, "osmdroid")
  val dirCreationSuccessful = cacheDir.mkdir()
  if (dirCreationSuccessful) {
    Configuration.getInstance().osmdroidTileCache = cacheDir
  }

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

  // Request location permissions and initialize myLocationOverlay
  LaunchedEffect(Unit) {
    val permissionGranted = requestLocationPermission(context)
    if (permissionGranted) {
      val myLocationProvider = GpsMyLocationProvider(context)
      myLocationProvider.startLocationProvider(null)
      myLocationOverlay = MyLocationNewOverlay(myLocationProvider, mapView)
      myLocationOverlay?.enableMyLocation()
      mapView.overlays.add(myLocationOverlay)
    }
  }

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

/**
 * A map view that displays the EPFL campus
 *
 * @param modifier the modifier to apply to the map view
 * @param onLoad the callback to call when the map is loaded (not used for the moment but could be
 *   useful for trickiers UX features)
 * @param markers the parsed markers list to display on the map
 */
@Composable
fun EPFLMapView(
    modifier: Modifier,
    onLoad: ((map: MapView) -> Unit)? = null,
    markers: List<MapMarkerData>
) {
  val mapViewState = rememberMapViewWithLifecycle()

  // A list to keep track of the markers in order to remove them
  // A clean wouldn't work as the EPFL buildings is part of the overlay
  markersList.forEach { marker -> mapViewState.overlays.remove(marker) }

  // For each markers, so for each task, add a marker on the map
  markers.forEach { markerData ->
    val marker = Marker(mapViewState)
    marker.title = markerData.name
    marker.snippet = markerData.description
    marker.position = markerData.position

    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    mapViewState.overlays.add(marker)

    markersList.add(marker)
  }

  // OSM maps are displayed as an Android view component
  AndroidView(
      factory = { mapViewState }, modifier = modifier, update = { view -> onLoad?.invoke(view) })
}

/**
 * Displays a permission dialog to request location permission
 *
 * @param context the context to display the dialog
 */
fun requestLocationPermission(context: Context): Boolean {
  if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
      PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(
        context as Activity,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        LOCATION_PERMISSION_REQUEST_CODE)
    return false
  }
  return true
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
