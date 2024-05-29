package com.github.se.assocify.ui.screens.event.maptab

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import kotlin.random.Random

// Initial position of the map (EPFL Agora)
val INITIAL_POSITION = GeoPoint(46.518726, 6.566613)
// Initial zoom of the map (Zoom made to be focused on the EPFL campus)
const val INITIAL_ZOOM = 17.0

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