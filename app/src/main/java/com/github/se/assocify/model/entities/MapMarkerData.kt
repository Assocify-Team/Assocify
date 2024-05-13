package com.github.se.assocify.model.entities

import org.osmdroid.util.GeoPoint

data class MapMarkerData(
  val name: String,
  val eventName: String,
  val position: GeoPoint,
  val description: String
)