package com.spywarescanner.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanningService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Background scanning service implementation
        // This would handle scheduled and real-time scanning
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
