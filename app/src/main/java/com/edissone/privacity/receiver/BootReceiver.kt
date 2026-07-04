package com.edissone.privacity.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.edissone.privacity.service.PrivacityForegroundService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("privacity_prefs", Context.MODE_PRIVATE)
            val backgroundScan = prefs.getBoolean("background_scan_enabled", false)

            if (backgroundScan) {
                val serviceIntent = Intent(context, PrivacityForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}