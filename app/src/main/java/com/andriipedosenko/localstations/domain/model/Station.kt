package com.andriipedosenko.localstations.domain.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Station(
    val mcc: Int,
    val mnc: Int,
    val lac: Int,
    val cellId: Long,
    val psc: Int,
    val rat: String,
    val latitude: Double,
    val longitude: Double
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(latitude, longitude)

    override fun getTitle(): String = rat

    override fun getSnippet(): String? = null

    override fun getZIndex(): Float? = null

}