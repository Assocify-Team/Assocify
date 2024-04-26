package com.github.se.assocify.ui.screens.event.map

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.se.assocify.R

import org.osmdroid.config.Configuration.*

lateinit var mMap: MapView
lateinit var controller: IMapController;
lateinit var mMyLocationOverlay: MyLocationNewOverlay;
/** A screen that displays a map of the event: location with the associated tasks. */
@Composable
fun EventMapScreen() {
  //setContentView(R.layout.activity_main)
  mMap = findViewById<MapView>(R.id.map)
  mMap.setTileSource(TileSourceFactory.MAPNIK)
}