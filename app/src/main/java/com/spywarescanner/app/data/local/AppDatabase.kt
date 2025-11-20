package com.spywarescanner.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spywarescanner.app.data.model.Alert
import com.spywarescanner.app.data.model.ScanResult
import com.spywarescanner.app.data.model.ScannedApp
import com.spywarescanner.app.data.model.Threat

@Database(
    entities = [
        ScannedApp::class,
        Threat::class,
        ScanResult::class,
        Alert::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scannedAppDao(): ScannedAppDao
    abstract fun threatDao(): ThreatDao
    abstract fun scanResultDao(): ScanResultDao
    abstract fun alertDao(): AlertDao
}
