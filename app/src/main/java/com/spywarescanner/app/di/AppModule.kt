package com.spywarescanner.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.spywarescanner.app.data.local.AppDatabase
import com.spywarescanner.app.data.local.AlertDao
import com.spywarescanner.app.data.local.ScanResultDao
import com.spywarescanner.app.data.local.ScannedAppDao
import com.spywarescanner.app.data.local.ThreatDao
import com.spywarescanner.app.data.repository.AlertRepository
import com.spywarescanner.app.data.repository.PreferencesRepository
import com.spywarescanner.app.data.repository.ScanRepository
import com.spywarescanner.app.detection.SpywareDetectionEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "spyware_scanner.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideScannedAppDao(database: AppDatabase): ScannedAppDao {
        return database.scannedAppDao()
    }

    @Provides
    @Singleton
    fun provideThreatDao(database: AppDatabase): ThreatDao {
        return database.threatDao()
    }

    @Provides
    @Singleton
    fun provideScanResultDao(database: AppDatabase): ScanResultDao {
        return database.scanResultDao()
    }

    @Provides
    @Singleton
    fun provideAlertDao(database: AppDatabase): AlertDao {
        return database.alertDao()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(dataStore: DataStore<Preferences>): PreferencesRepository {
        return PreferencesRepository(dataStore)
    }

    @Provides
    @Singleton
    fun provideScanRepository(
        @ApplicationContext context: Context,
        scannedAppDao: ScannedAppDao,
        threatDao: ThreatDao,
        scanResultDao: ScanResultDao,
        detectionEngine: SpywareDetectionEngine
    ): ScanRepository {
        return ScanRepository(context, scannedAppDao, threatDao, scanResultDao, detectionEngine)
    }

    @Provides
    @Singleton
    fun provideAlertRepository(alertDao: AlertDao): AlertRepository {
        return AlertRepository(alertDao)
    }

    @Provides
    @Singleton
    fun provideSpywareDetectionEngine(@ApplicationContext context: Context): SpywareDetectionEngine {
        return SpywareDetectionEngine(context)
    }
}
