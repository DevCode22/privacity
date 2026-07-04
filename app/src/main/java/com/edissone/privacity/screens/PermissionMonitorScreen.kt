package com.edissone.privacity.screens

import android.Manifest
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PermissionApp(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean,
    val grantedPermissions: Set<String>,
    val camera: Boolean,
    val microphone: Boolean,
    val location: Boolean,
    val sms: Boolean,
    val contacts: Boolean,
    val phone: Boolean,
    val storage: Boolean,
    val calendar: Boolean,
    val sensors: Boolean
)

data class PermissionCategory(
    val key: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val bgColor: Color,
    val check: (PermissionApp) -> Boolean
)

private val PERMISSION_MAP = mapOf(
    "android.permission.CAMERA" to "camera",
    "android.permission.RECORD_AUDIO" to "microphone",
    "android.permission.ACCESS_FINE_LOCATION" to "location",
    "android.permission.ACCESS_COARSE_LOCATION" to "location",
    "android.permission.SEND_SMS" to "sms",
    "android.permission.READ_SMS" to "sms",
    "android.permission.RECEIVE_SMS" to "sms",
    "android.permission.READ_CONTACTS" to "contacts",
    "android.permission.WRITE_CONTACTS" to "contacts",
    "android.permission.READ_PHONE_STATE" to "phone",
    "android.permission.READ_CALL_LOG" to "phone",
    "android.permission.PROCESS_OUTGOING_CALLS" to "phone",
    "android.permission.READ_EXTERNAL_STORAGE" to "storage",
    "android.permission.WRITE_EXTERNAL_STORAGE" to "storage",
    "android.permission.MANAGE_EXTERNAL_STORAGE" to "storage",
    "android.permission.READ_CALENDAR" to "calendar",
    "android.permission.WRITE_CALENDAR" to "calendar",
    "android.permission.BODY_SENSORS" to "sensors"
)

private val TRACKED_PERMISSIONS = PERMISSION_MAP.keys

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionMonitorScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current

    var allApps by remember { mutableStateOf<List<PermissionApp>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedApp by remember { mutableStateOf<PermissionApp?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    val bg = Color(0xFFF5F5F7)
    val white = Color.White
    val orange = Color(0xFFFF8C42)
    val orangeSoft = Color(0xFFFFE8D6)
    val blue = Color(0xFF4A90D9)
    val blueSoft = Color(0xFFD6EAFF)
    val red = Color(0xFFFF3B30)
    val redSoft = Color(0xFFFFD6D6)
    val green = Color(0xFF34C759)
    val purple = Color(0xFFAF52DE)
    val purpleSoft = Color(0xFFE8D6F5)
    val teal = Color(0xFF5AC8FA)
    val tealSoft = Color(0xFFD6F0FA)
    val yellow = Color(0xFFFFCC00)
    val yellowSoft = Color(0xFFFFF5CC)
    val textDark = Color(0xFF1C1C1E)
    val textGray = Color(0xFF6E6E73)
    val cardBorder = Color(0xFFE5E5EA)

    val categories = listOf(
        PermissionCategory("camera", "Câmara", Icons.Filled.CameraAlt, red, redSoft) { it.camera },
        PermissionCategory("microphone", "Microfone", Icons.Filled.Mic, red, redSoft) { it.microphone },
        PermissionCategory("location", "Localização", Icons.Filled.LocationOn, orange, orangeSoft) { it.location },
        PermissionCategory("sms", "SMS", Icons.Filled.Sms, orange, orangeSoft) { it.sms },
        PermissionCategory("contacts", "Contactos", Icons.Filled.Contacts, blue, blueSoft) { it.contacts },
        PermissionCategory("phone", "Telefone", Icons.Filled.Phone, purple, purpleSoft) { it.phone },
        PermissionCategory("storage", "Armazenamento", Icons.Filled.Storage, teal, tealSoft) { it.storage },
        PermissionCategory("calendar", "Calendário", Icons.Filled.CalendarMonth, yellow, yellowSoft) { it.calendar },
        PermissionCategory("sensors", "Sensores", Icons.Filled.Biotech, purple, purpleSoft) { it.sensors }
    )

    LaunchedEffect(Unit) {
        isLoading = true
        val apps = loadAppsWithGrantedPermissions(context)
        allApps = apps
        isLoading = false

        // Detetar novas permissoes concedidas desde a última verificacao
        val prefs = context.getSharedPreferences("privacity_prefs", Context.MODE_PRIVATE)
        val lastKnown = prefs.getStringSet("last_known_permissions", emptySet()) ?: emptySet()
        val currentKeys = mutableSetOf<String>()

        for (app in apps) {
            for (perm in app.grantedPermissions) {
                val key = "${app.packageName}|$perm"
                currentKeys.add(key)
                if (key !in lastKnown) {
                    // Nova permissao concedida!
                    sendPermissionNotification(context, app.appName, perm.substringAfterLast("."))
                }
            }
        }

        // Guardar estado atual
        prefs.edit().putStringSet("last_known_permissions", currentKeys).apply()
    }

    Scaffold(containerColor = bg) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Surface(color = white, shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedCategory != null) {
                        IconButton(onClick = { selectedCategory = null }) { Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = textDark) }
                    } else {
                        IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = textDark) }
                    }
                    Spacer(Modifier.width(4.dp))
                    if (selectedCategory != null) {
                        val cat = categories.find { it.key == selectedCategory }
                        if (cat != null) {
                            Icon(cat.icon, contentDescription = null, tint = cat.color, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(cat.name, color = textDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                val count = allApps.count(cat.check)
                                Text("$count aplicações com acesso", color = textGray, fontSize = 12.sp)
                            }
                        }
                    } else {
                        Column {
                            Text("Monitor de Permissões", color = textDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Apps com permissões CONCEDIDAS", color = textGray, fontSize = 12.sp)
                        }
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = blue)
                        Spacer(Modifier.height(12.dp))
                        Text("A carregar permissões...", color = textGray, fontSize = 14.sp)
                    }
                }
            } else if (selectedCategory == null) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                    Text("Selecione uma permissão para ver as aplicações que a têm CONCEDIDA", color = textGray, fontSize = 13.sp)
                    Spacer(Modifier.height(14.dp))
                    categories.chunked(2).forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { cat ->
                                val appCount = allApps.count(cat.check)
                                CategoryCard(modifier = Modifier.weight(1f), category = cat, appCount = appCount,
                                    onClick = { selectedCategory = cat.key }, white = white, textDark = textDark, textGray = textGray, cardBorder = cardBorder)
                            }
                            if (row.size < 2) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = white), elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Resumo", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(Modifier.height(10.dp))
                            categories.forEach { cat ->
                                val count = allApps.count(cat.check)
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(cat.icon, contentDescription = null, tint = cat.color, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(cat.name, color = textDark, fontSize = 13.sp)
                                    }
                                    Text("$count apps", color = if (count > 0) cat.color else textGray, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            } else {
                val cat = categories.find { it.key == selectedCategory }!!
                val filteredApps = allApps.filter(cat.check).sortedBy { it.appName }
                if (filteredApps.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(64.dp), tint = green.copy(alpha = 0.5f))
                            Spacer(Modifier.height(12.dp))
                            Text("Nenhuma aplicação com esta permissão concedida", color = textGray, fontSize = 16.sp)
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredApps) { app ->
                            PermissionAppCard(app = app, category = cat, onClick = { selectedApp = app; showDetailDialog = true },
                                white = white, textDark = textDark, textGray = textGray, green = green, red = red, cardBorder = cardBorder)
                        }
                    }
                }
            }
        }
    }

    if (showDetailDialog && selectedApp != null) {
        val app = selectedApp!!
        AlertDialog(
            onDismissRequest = { showDetailDialog = false },
            containerColor = white, shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Android, contentDescription = null, tint = blue, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(app.appName, fontWeight = FontWeight.Bold, color = textDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    DetailRow("Pacote", app.packageName, textGray, textDark)
                    DetailRow("App de Sistema", if (app.isSystemApp) "Sim" else "Não", textGray, textDark)
                    DetailRow("Total Permissões Concedidas", "${app.grantedPermissions.size}", textGray, textDark)
                    Spacer(Modifier.height(12.dp))
                    Text("Permissões Concedidas", fontWeight = FontWeight.Bold, color = textDark, fontSize = 14.sp)
                    HorizontalDivider(color = cardBorder)
                    Spacer(Modifier.height(6.dp))
                    PermissionRow("Câmara", app.camera, green, red, textDark)
                    PermissionRow("Microfone", app.microphone, green, red, textDark)
                    PermissionRow("Localização", app.location, green, red, textDark)
                    PermissionRow("SMS", app.sms, green, red, textDark)
                    PermissionRow("Contactos", app.contacts, green, red, textDark)
                    PermissionRow("Telefone", app.phone, green, red, textDark)
                    PermissionRow("Armazenamento", app.storage, green, red, textDark)
                    PermissionRow("Calendário", app.calendar, green, red, textDark)
                    PermissionRow("Sensores", app.sensors, green, red, textDark)
                    val count = listOf(app.camera, app.microphone, app.location, app.sms, app.contacts, app.phone, app.storage, app.calendar, app.sensors).count { it }
                    if (count >= 4) {
                        Spacer(Modifier.height(12.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = orangeSoft), shape = RoundedCornerShape(10.dp)) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Warning, contentDescription = null, tint = orange, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Esta app tem acesso a $count permissões sensíveis!", color = textDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showDetailDialog = false }) { Text("Fechar", color = blue, fontWeight = FontWeight.SemiBold) } }
        )
    }
}

@Composable
private fun CategoryCard(modifier: Modifier, category: PermissionCategory, appCount: Int, onClick: () -> Unit, white: Color, textDark: Color, textGray: Color, cardBorder: Color) {
    Card(modifier = modifier, onClick = onClick, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = white), elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)) {
        Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(category.bgColor), contentAlignment = Alignment.Center) {
                Icon(category.icon, contentDescription = null, tint = category.color, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(category.name, color = textDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text("$appCount apps", color = if (appCount > 0) category.color else textGray, fontWeight = FontWeight.Medium, fontSize = 13.sp)
        }
    }
}

@Composable
private fun PermissionAppCard(app: PermissionApp, category: PermissionCategory, onClick: () -> Unit, white: Color, textDark: Color, textGray: Color, green: Color, red: Color, cardBorder: Color) {
    Card(onClick = onClick, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = white), elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(10.dp)).background(category.bgColor), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Android, contentDescription = null, tint = category.color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(app.appName, color = textDark, fontWeight = FontWeight.Medium, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(app.packageName, color = textGray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(category.icon, contentDescription = null, tint = category.color, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun PermissionRow(label: String, hasIt: Boolean, green: Color, red: Color, textDark: Color) {
    Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(if (hasIt) Icons.Filled.CheckCircle else Icons.Filled.Cancel, contentDescription = null, tint = if (hasIt) red else green, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, color = textDark, fontSize = 13.sp)
        if (hasIt) { Spacer(Modifier.width(6.dp)); Text("CONCEDIDA", color = red, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
        else { Spacer(Modifier.width(6.dp)); Text("NÃO", color = green, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun DetailRow(label: String, value: String, textGray: Color, textValue: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = textGray, fontSize = 12.sp)
        Text(value, color = textValue, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private suspend fun loadAppsWithGrantedPermissions(context: Context): List<PermissionApp> = withContext(Dispatchers.IO) {
    val pm = context.packageManager
    val apps = mutableListOf<PermissionApp>()

    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
    } else { PackageManager.GET_PERMISSIONS }

    @Suppress("DEPRECATION")
    val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pm.getInstalledPackages(flags as PackageManager.PackageInfoFlags)
    } else { pm.getInstalledPackages(flags as Int) }

    for (pkg in packages) {
        try {
            val appInfo = pkg.applicationInfo ?: continue
            val appName = pm.getApplicationLabel(appInfo).toString()
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val requestedPerms = pkg.requestedPermissions?.toList() ?: emptyList()
            val grantedPermissions = mutableSetOf<String>()

            for (perm in requestedPerms) {
                if (perm in TRACKED_PERMISSIONS && pm.checkPermission(perm, pkg.packageName) == PackageManager.PERMISSION_GRANTED) {
                    grantedPermissions.add(perm)
                }
            }

            if (grantedPermissions.isEmpty()) continue

            apps.add(PermissionApp(
                packageName = pkg.packageName, appName = appName, isSystemApp = isSystem, grantedPermissions = grantedPermissions,
                camera = grantedPermissions.contains("android.permission.CAMERA"),
                microphone = grantedPermissions.contains("android.permission.RECORD_AUDIO"),
                location = grantedPermissions.any { it.contains("ACCESS_FINE_LOCATION") || it.contains("ACCESS_COARSE_LOCATION") },
                sms = grantedPermissions.any { it.contains("SEND_SMS") || it.contains("READ_SMS") || it.contains("RECEIVE_SMS") },
                contacts = grantedPermissions.any { it.contains("READ_CONTACTS") || it.contains("WRITE_CONTACTS") },
                phone = grantedPermissions.any { it.contains("READ_PHONE_STATE") || it.contains("READ_CALL_LOG") || it.contains("PROCESS_OUTGOING_CALLS") },
                storage = grantedPermissions.any { it.contains("READ_EXTERNAL_STORAGE") || it.contains("WRITE_EXTERNAL_STORAGE") || it.contains("MANAGE_EXTERNAL_STORAGE") },
                calendar = grantedPermissions.any { it.contains("READ_CALENDAR") || it.contains("WRITE_CALENDAR") },
                sensors = grantedPermissions.contains("android.permission.BODY_SENSORS")
            ))
        } catch (_: Exception) { continue }
    }
    apps
}

private fun sendPermissionNotification(context: Context, appName: String, permission: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
    }
    val notification = NotificationCompat.Builder(context, "privacity_alerts")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Nova Permissão Concedida")
        .setContentText("$appName obteve acesso a: $permission")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()
    NotificationManagerCompat.from(context).notify(2000 + System.currentTimeMillis().toInt(), notification)
}