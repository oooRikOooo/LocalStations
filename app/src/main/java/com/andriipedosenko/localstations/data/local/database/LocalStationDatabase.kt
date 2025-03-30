package com.andriipedosenko.localstations.data.local.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class LocalStationDatabase @Inject constructor(
    private val context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "stations.db"
        private const val DATABASE_VERSION = 1
    }

    private val databasePath = context.getDatabasePath(DATABASE_NAME).path

    var database: SQLiteDatabase? = null

    init {
        if (checkDatabase()) {
            openDatabase()
        } else {
            writableDatabase
            copyDatabaseFromAssets()
            openDatabase()
        }
    }

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    private fun copyDatabaseFromAssets() {
        try {
            val inputStream = context.assets.open(DATABASE_NAME)
            val outputStream = FileOutputStream(databasePath)

            inputStream.copyTo(outputStream)

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    private fun checkDatabase(): Boolean {
        val db = try {
            val dbFile = context.getDatabasePath(DATABASE_NAME)

            SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READONLY)

        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }

        db?.close()

        return db != null
    }

    private fun openDatabase() {
        database = try {
            SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE)
        } catch (exception: SQLiteException) {
            exception.printStackTrace()
            null
        }
    }

    override fun close() {
        database?.close()
        super.close()
    }
}