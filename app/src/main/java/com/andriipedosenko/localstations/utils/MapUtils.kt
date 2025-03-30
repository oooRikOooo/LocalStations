package com.andriipedosenko.localstations.utils

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLngBounds

fun GoogleMap.zoomLevel(): Float {
    return cameraPosition.zoom
}

fun GoogleMap.visibleCoordinates(): LatLngBounds {
    val projection: Projection = projection

    val visibleRegion = projection.visibleRegion

    return visibleRegion.latLngBounds
}