package com.github.se.assocify.ui.screens.event.maptab

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.entities.MapMarkerData
import org.osmdroid.util.GeoPoint

class EventMapViewModel : ViewModel() {

  private val _markers = MutableLiveData<MutableList<MapMarkerData>>()
  val markers: MutableLiveData<MutableList<MapMarkerData>> get() = _markers

  init {
    // Initialize the markers list with the test marker
    val testMarker = MapMarkerData(
      name = "EPFL0",
      eventName = "Test Event",
      position = GeoPoint(46.518726, 6.566613),
      description = "Test Description"
    )
    _markers.value = mutableListOf(testMarker)
  }

  fun addMarker(marker: MapMarkerData) {
    _markers.value?.apply {
      add(marker)
      _markers.value = this
    }
  }

  fun removeMarker(marker: MapMarkerData) {
    _markers.value?.apply {
      remove(marker)
      _markers.value = this
    }
  }
}