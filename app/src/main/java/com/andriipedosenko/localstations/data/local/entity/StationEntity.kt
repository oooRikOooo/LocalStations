package com.andriipedosenko.localstations.data.local.entity

data class StationEntity(
    val mcc: Int,
    val mnc: Int,
    val lac: Int,
    val cellId: Long,
    val psc: Int,
    val rat: String,
    val latitude: Double,
    val longitude: Double
)
