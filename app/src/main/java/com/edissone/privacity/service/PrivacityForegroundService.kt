package com.edissone.privacity.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class PrivacityForegroundService : Service() {

    companion object {
        const val CHANNEL_BACKGROUND_ID = "privacity_background"
        const val CHANNEL_ALERTS_ID = "privacity_alerts"
        const val NOTIFICATION_ID_BACKGROUND = 1
        var isRunning = false
    }

    private val trackedPermissions = listOf(
        "android.permission.CAMERA",
        "android.permission.RECORD_AUDIO",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.READ_SMS",
        "android.permission.RECEIVE_SMS",
        "android.permission.READ_CONTACTS",
        "android.permission.READ_CALL_LOG",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.SYSTEM_ALERT_WINDOW",
        "android.permission.BIND_ACCESSIBILITY_SERVICE",
        "android.permission.INSTALL_PACKAGES"
    )

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED -> {
                    val packageName = intent.data?.schemeSpecificPart ?: return
                    if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) return
                    analyzeNewApp(context, packageName)
                }
                Intent.ACTION_PACKAGE_FULLY_REMOVED -> { /* cleanup opcional */ }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_BACKGROUND_ID)
            .setContentTitle("Privacity")
            .setContentText("Monitorizando aplicações e permissões...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID_BACKGROUND, notification)

        // Registar receiver para novas apps
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        registerReceiver(packageReceiver, filter)

        // Fazer verificação inicial de permissões
        checkPermissionChanges(this)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        isRunning = false
        try { unregisterReceiver(packageReceiver) } catch (_: Exception) {}
        super.onDestroy()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val bgChannel = NotificationChannel(
                CHANNEL_BACKGROUND_ID,
                "Privacity - Monitorização",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificação persistente para serviço em segundo plano"
            }
            val alertChannel = NotificationChannel(
                CHANNEL_ALERTS_ID,
                "Privacity - Alertas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas de segurança e privacidade"
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(bgChannel)
            nm.createNotificationChannel(alertChannel)
        }
    }

    private fun analyzeNewApp(context: Context, packageName: String) {
        try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val appName = pm.getApplicationLabel(appInfo).toString()
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            if (isSystem) return // ignorar apps de sistema

            val installer = pm.getInstallerPackageName(packageName)
            val pkgInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            }

            val perms = pkgInfo.requestedPermissions?.toList() ?: emptyList()
            val highRiskPerms = perms.filter { it in trackedPermissions }
            val permSet = highRiskPerms.toSet()

            val hasAccessibility = permSet.contains("android.permission.BIND_ACCESSIBILITY_SERVICE")
            val hasOverlay = permSet.contains("android.permission.SYSTEM_ALERT_WINDOW")
            val hasInstallPackages = permSet.contains("android.permission.INSTALL_PACKAGES")

            val issues = mutableListOf<String>()

            if (highRiskPerms.isNotEmpty()) {
                issues.add("Permissões: ${highRiskPerms.map { it.substringAfterLast(".") }.joinToString(", ")}")
            }
            if (hasAccessibility && hasOverlay) {
                issues.add("⚠ Combinação perigosa: Accessibility + Overlay")
            }
            if (hasInstallPackages && hasOverlay) {
                issues.add("⚠ Overlay + Instalação de APKs")
            }
            if (!isSystem && installer != "com.android.vending" && installer != null) {
                issues.add("⚠ Instalada fora da Play Store: $installer")
            }
            if (installer == null && !isSystem) {
                issues.add("⚠ Instalador desconhecido (sideload)")
            }

            if (issues.isNotEmpty()) {
                val details = issues.joinToString("\n")
                showNotification(context, "App Detectada: $appName", details)
            }
        } catch (_: Exception) { /* ignorar */ }
    }

    fun checkPermissionChanges(context: Context) {
        val prefs = context.getSharedPreferences("privacity_prefs", Context.MODE_PRIVATE)
        val lastKnown = prefs.getStringSet("last_known_permissions", emptySet()) ?: emptySet()
        val currentPerms = mutableSetOf<String>()
        val pm = context.packageManager

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_PERMISSIONS
        }

        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledPackages(flags as PackageManager.PackageInfoFlags)
        } else {
            @Suppress("DEPRECATION")
            pm.getInstalledPackages(flags as Int)
        }

        for (pkg in packages) {
            val appInfo = pkg.applicationInfo ?: continue
            for (perm in (pkg.requestedPermissions?.toList() ?: emptyList())) {
                if (perm in trackedPermissions &&
                    pm.checkPermission(perm, pkg.packageName) == PackageManager.PERMISSION_GRANTED
                ) {
                    val key = "${pkg.packageName}|$perm"
                    currentPerms.add(key)
                    if (key !in lastKnown) {
                        val appName = pm.getApplicationLabel(appInfo).toString()
                        showNotification(
                            context,
                            "Nova Permissão Concedida",
                            "$appName obteve acesso a: ${perm.substringAfterLast(".")}"
                        )
                    }
                }
            }
        }
        prefs.edit().putStringSet("last_known_permissions", currentPerms).apply()
    }

    private fun showNotification(context: Context, title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) return
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message.take(200))
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(
            (1000 + System.currentTimeMillis() % 100000).toInt(),
            notification
        )
    }
}