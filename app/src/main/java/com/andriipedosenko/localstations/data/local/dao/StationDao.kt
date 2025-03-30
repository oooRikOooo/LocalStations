package com.andriipedosenko.localstations.data.local.dao

import android.content.ContentValues
import com.andriipedosenko.localstations.data.local.database.LocalStationDatabase
import com.andriipedosenko.localstations.data.local.database.StationEntry
import com.andriipedosenko.localstations.data.local.entity.StationEntity
import com.google.android.gms.maps.model.LatLngBounds
import javax.inject.Inject

class StationDao @Inject constructor(
    private val localStationDatabase: LocalStationDatabase
) {

    fun insertStation(stationEntity: StationEntity): Long {
        val db = localStationDatabase.database ?: return -1

        val values = ContentValues().apply {
            put(StationEntry.COLUMN_MCC, stationEntity.mcc)
            put(StationEntry.COLUMN_MNC, stationEntity.mnc)
            put(StationEntry.COLUMN_LAC, stationEntity.lac)
            put(StationEntry.COLUMN_CELL_ID, stationEntity.cellId)
            put(StationEntry.COLUMN_PSC, stationEntity.psc)
            put(StationEntry.COLUMN_RAT, stationEntity.rat)
            put(StationEntry.COLUMN_LAT, stationEntity.latitude)
            put(StationEntry.COLUMN_LON, stationEntity.longitude)
        }

        return db.insert(StationEntry.TABLE_NAME, null, values)
    }

    fun updateStation(stationEntity: StationEntity): Int {
        val db = localStationDatabase.database ?: return 0

        val values = ContentValues().apply {
            put(StationEntry.COLUMN_MCC, stationEntity.mcc)
            put(StationEntry.COLUMN_MNC, stationEntity.mnc)
            put(StationEntry.COLUMN_LAC, stationEntity.lac)
            put(StationEntry.COLUMN_PSC, stationEntity.psc)
            put(StationEntry.COLUMN_RAT, stationEntity.rat)
            put(StationEntry.COLUMN_LAT, stationEntity.latitude)
            put(StationEntry.COLUMN_LON, stationEntity.longitude)
        }

        val selection = "${StationEntry.COLUMN_CELL_ID} = ?"
        val selectionArgs = arrayOf(stationEntity.cellId.toString())

        return db.update(StationEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    fun deleteStation(id: Long): Int {
        val db = localStationDatabase.database ?: return 0

        val selection = "${StationEntry.COLUMN_CELL_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        return db.delete(StationEntry.TABLE_NAME, selection, selectionArgs)
    }

    fun getStationsWithinBounds(
        bounds: LatLngBounds,
        limit: Int
    ): List<StationEntity> {
        try {
            val db = localStationDatabase.database ?: return emptyList()

            val projection = arrayOf(
                StationEntry.COLUMN_MCC,
                StationEntry.COLUMN_MNC,
                StationEntry.COLUMN_LAC,
                StationEntry.COLUMN_CELL_ID,
                StationEntry.COLUMN_PSC,
                StationEntry.COLUMN_RAT,
                StationEntry.COLUMN_LAT,
                StationEntry.COLUMN_LON
            )

            val selection =
                "${StationEntry.COLUMN_LAT} BETWEEN ? AND ? AND ${StationEntry.COLUMN_LON} BETWEEN ? AND ?"

            val selectionArgs = arrayOf(
                bounds.southwest.latitude.toString(),
                bounds.northeast.latitude.toString(),
                bounds.southwest.longitude.toString(),
                bounds.northeast.longitude.toString()
            )

            val cursor = db.query(
                StationEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                StationEntry.COLUMN_CELL_ID,
                limit.toString()
            )

            val stationEntityList = mutableListOf<StationEntity>()

            with(cursor) {
                while (moveToNext()) {

                    val mcc = getInt(getColumnIndexOrThrow(StationEntry.COLUMN_MCC))
                    val mnc = getInt(getColumnIndexOrThrow(StationEntry.COLUMN_MNC))
                    val lac = getInt(getColumnIndexOrThrow(StationEntry.COLUMN_LAC))
                    val cellId = getLong(getColumnIndexOrThrow(StationEntry.COLUMN_CELL_ID))
                    val psc = getInt(getColumnIndexOrThrow(StationEntry.COLUMN_PSC))
                    val rat = getString(getColumnIndexOrThrow(StationEntry.COLUMN_RAT))
                    val latitude = getDouble(getColumnIndexOrThrow(StationEntry.COLUMN_LAT))
                    val longitude = getDouble(getColumnIndexOrThrow(StationEntry.COLUMN_LON))

                    val stationEntity = StationEntity(
                        mcc = mcc,
                        mnc = mnc,
                        lac = lac,
                        cellId = cellId,
                        psc = psc,
                        rat = rat,
                        latitude = latitude,
                        longitude = longitude
                    )

                    stationEntityList.add(stationEntity)
                }
            }

            cursor.close()

            return stationEntityList
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}