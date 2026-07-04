package com.edissone.privacity.screens

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.edissone.privacity.R
import com.edissone.privacity.model.AlertItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.FileReader
import java.util.Locale


//  CORES E ESTILOS GLOBAIS

private val bg = Color(0xFFF5F5F7)
private val white = Color.White
private val orange = Color(0xFFFF8C42)
private val blue = Color(0xFF4A90D9)
private val green = Color(0xFF34C759)
private val textDark = Color(0xFF1C1C1E)
private val textGray = Color(0xFF6E6E73)


//Funcao que gera Alertas

fun generateRealAlerts(context: Context): List<AlertItem> {
    val alerts = mutableListOf<AlertItem>()
    val pm = context.packageManager

    // 1. Verificar Apps Suspeitas (sideload ou muitas permissoes)
    try {
        @Suppress("DEPRECATION")
        val packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        var suspiciousCount = 0
        for (pkg in packages) {
            val appInfo = pkg.applicationInfo ?: continue
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            @Suppress("DEPRECATION")
            val installer = pm.getInstallerPackageName(pkg.packageName)

            if (!isSystem && installer == null) {
                suspiciousCount++
            }
        }
        if (suspiciousCount > 0) {
            alerts.add(AlertItem(
                id = "suspicious_apps",
                title = "Apps de Origem Desconhecida",
                description = "Encontradas $suspiciousCount aplicações instaladas manualmente (sideload).",
                icon = Icons.Outlined.Android,
                color = orange,
                timestamp = "Agora",
                priority = "warning",
                action = "app_detector"
            ))
        }
    } catch (_: Exception) {}

    // 2. Verificar Trafego de Rede Elevado (leitura de /proc/net/dev)
    try {
        val bytes = readProcNetDev().sumOf { it.rxBytes + it.txBytes }
        if (bytes > 100 * 1024 * 1024) { // Exemplo: > 100MB
            alerts.add(AlertItem(
                id = "high_network",
                title = "Tráfego de Rede Elevado",
                description = "O dispositivo consumiu uma quantidade significativa de dados recentemente.",
                icon = Icons.Outlined.TrendingUp,
                color = blue,
                timestamp = "Recentemente",
                priority = "info",
                action = "network"
            ))
        }
    } catch (_: Exception) {}

    // 3. Alerta de Segurança Generico (Exemplo)
    alerts.add(AlertItem(
        id = "security_tip",
        title = "Dica de Privacidade",
        description = "Lembre-se de verificar as permissões de localização das suas apps.",
        icon = Icons.Outlined.Shield,
        color = green,
        timestamp = "Hoje",
        priority = "low",
        action = "permission"
    ))

    return alerts.sortedBy {
        when(it.priority) {
            "critical" -> 0
            "warning" -> 1
            "info" -> 2
            else -> 3
        }
    }
}

private data class AlertProcNetEntry(val iface: String, val rxBytes: Long, val txBytes: Long)

private fun readProcNetDev(): List<AlertProcNetEntry> {
    val entries = mutableListOf<AlertProcNetEntry>()
    try {
        BufferedReader(FileReader("/proc/net/dev")).use { reader ->
            reader.readLine()
            reader.readLine()
            reader.forEachLine { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty()) return@forEachLine
                val colonIdx = trimmed.indexOf(':')
                if (colonIdx == -1) return@forEachLine
                val parts = trimmed.substring(colonIdx + 1).trim().split("\\s+".toRegex())
                if (parts.size >= 9) {
                    val rxBytes = parts[0].toLongOrNull() ?: 0L
                    val txBytes = parts[8].toLongOrNull() ?: 0L
                    entries.add(AlertProcNetEntry(trimmed.substring(0, colonIdx), rxBytes, txBytes))
                }
            }
        }
    } catch (_: Exception) {}
    return entries
}


// Centro de Alertas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertCenterScreen(
    onBack: () -> Unit,
    onNavigateTo: (String) -> Unit
) {
    val context = LocalContext.current
    var alerts by remember { mutableStateOf(listOf<AlertItem>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        alerts = withContext(Dispatchers.IO) { generateRealAlerts(context) }
        isLoading = false
    }

    Scaffold(
        containerColor = bg,
        topBar = {
            Surface(color = white, shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = textDark)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.alert_center_title),
                        color = textDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = blue)
            }
        } else if (alerts.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = green, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("Tudo Seguro!", color = textDark, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Não foram detectados alertas de privacidade.", color = textGray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        "Alertas Identificados",
                        color = textDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                items(alerts) { alert ->
                    AlertItemCard(alert) {
                        if (alert.action.isNotEmpty()) {
                            onNavigateTo(alert.action)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertItemCard(alert: AlertItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(alert.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(alert.icon, contentDescription = null, tint = alert.color, modifier = Modifier.size(24.dp))
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(alert.title, color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(alert.timestamp, color = textGray, fontSize = 11.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(alert.description, color = textGray, fontSize = 13.sp, lineHeight = 18.sp)

                if (alert.action.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        alert.actionLabel,
                        color = blue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
