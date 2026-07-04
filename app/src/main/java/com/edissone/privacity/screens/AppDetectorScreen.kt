package com.edissone.privacity.screens

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
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

data class AppAnalysis(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean,
    val installerPackage: String?,
    val isFromPlayStore: Boolean,
    val hasExcessivePermissions: Boolean,
    val excessivePermissions: List<String>,
    val dangerousCombinations: List<String>,
    val permissionIncoherence: List<String>,
    val isSuspicious: Boolean,
    val riskScore: Int,
    val reasons: List<String>
)

private val HIGH_RISK_PERMISSIONS = listOf(
    "android.permission.READ_SMS", "android.permission.RECEIVE_SMS", "android.permission.SEND_SMS",
    "android.permission.READ_CONTACTS", "android.permission.ACCESS_FINE_LOCATION",
    "android.permission.ACCESS_COARSE_LOCATION", "android.permission.CAMERA",
    "android.permission.RECORD_AUDIO", "android.permission.READ_CALL_LOG",
    "android.permission.PROCESS_OUTGOING_CALLS", "android.permission.READ_EXTERNAL_STORAGE",
    "android.permission.MANAGE_EXTERNAL_STORAGE", "android.permission.SYSTEM_ALERT_WINDOW",
    "android.permission.BIND_ACCESSIBILITY_SERVICE", "android.permission.INSTALL_PACKAGES",
    "android.permission.RECEIVE_BOOT_COMPLETED"
)



//Traduzir detalhes tecnicos
private val PERMISSION_FRIENDLY_NAMES = mapOf(
    "READ_SMS"                     to "Ler as suas mensagens SMS",
    "RECEIVE_SMS"                  to "Receber notificação de novos SMS",
    "SEND_SMS"                     to "Enviar mensagens SMS",
    "READ_CONTACTS"                to "Aceder aos seus contactos",
    "ACCESS_FINE_LOCATION"         to "Saber a sua localização exata (GPS)",
    "ACCESS_COARSE_LOCATION"       to "Saber a sua localização aproximada",
    "CAMERA"                       to "Usar a câmara",
    "RECORD_AUDIO"                 to "Gravar áudio com o microfone",
    "READ_CALL_LOG"                to "Ver o seu histórico de chamadas",
    "PROCESS_OUTGOING_CALLS"       to "Interferir nas suas chamadas",
    "READ_EXTERNAL_STORAGE"        to "Ler ficheiros do seu armazenamento",
    "MANAGE_EXTERNAL_STORAGE"      to "Gerir todos os seus ficheiros",
    "SYSTEM_ALERT_WINDOW"          to "Mostrar janelas sobre outras apps (como pop-ups)",
    "BIND_ACCESSIBILITY_SERVICE"   to "Controlar o telemóvel por si (como se fosse um assistente automático)",
    "INSTALL_PACKAGES"             to "Instalar novas aplicações sem o seu conhecimento",
    "RECEIVE_BOOT_COMPLETED"       to "Ligar sozinha quando o telemóvel reinicia"
)

//Traducao das descricoes
private val DANGEROUS_COMBINATIONS = listOf(
    Pair(
        listOf("BIND_ACCESSIBILITY_SERVICE", "SYSTEM_ALERT_WINDOW"),
        "Pode criar pop-ups e controlar o ecrã por si, sem que perceba que outra app está a ser usada"
    ),
    Pair(
        listOf("BIND_ACCESSIBILITY_SERVICE", "READ_SMS"),
        "Pode ler as suas mensagens (incluindo códigos de 2FA) automaticamente"
    ),
    Pair(
        listOf("SYSTEM_ALERT_WINDOW", "INSTALL_PACKAGES"),
        "Pode mostrar pop-ups falsos que parecem legítimos para o(a) enganar a instalar apps"
    ),
    Pair(
        listOf("RECEIVE_BOOT_COMPLETED", "SYSTEM_ALERT_WINDOW"),
        "Volta a mostrar pop-ups assim que o telemóvel reinicia, sem precisar de abrir a app"
    ),
    Pair(
        listOf("BIND_ACCESSIBILITY_SERVICE", "RECORD_AUDIO"),
        "Pode gravar o que se passa à sua volta e o que faz no ecrã ao mesmo tempo"
    ),
    Pair(
        listOf("BIND_ACCESSIBILITY_SERVICE", "ACCESS_FINE_LOCATION"),
        "Pode saber onde está e controlar o telemóvel ao mesmo tempo"
    ),
    Pair(
        listOf("INSTALL_PACKAGES", "SYSTEM_ALERT_WINDOW"),
        "Consegue instalar apps escondidas por detrás de pop-ups – método muito usado por malware"
    ),
    Pair(
        listOf("READ_SMS", "RECEIVE_SMS", "BIND_ACCESSIBILITY_SERVICE"),
        "Consegue ler os seus SMS (códigos de banco, 2FA) e responder automaticamente"
    ),
)

private val TRUSTED_SYSTEM_PACKAGES = listOf(
    "com.android.", "com.google.android.", "com.qualcomm.", "com.mediatek.",
    "com.samsung.android.", "com.xiaomi.", "com.oneplus.", "com.huawei.",
    "com.lge.", "com.motorola.", "com.sony."
)

private val TRUSTED_DEVELOPERS = listOf(
    "com.google.", "com.microsoft.", "com.slack.", "com.twitter.",
    "com.facebook.", "com.instagram.", "com.whatsapp.",
    "com.spotify.", "com.netflix.", "com.amazon.",
    "org.telegram.", "com.skype.", "com.dropbox.",
    "com.android.chrome", "org.mozilla.firefox",
    "com.duolingo.", "com.uber.", "com.airbnb.",
    "com.discord.", "com.reddit.", "com.tiktok.",
    "com.snapchat.", "com.pinterest.", "com.linkedin.",
    "com.edissone."
)

const val NOTIFICATION_CHANNEL_ALERTS = "privacity_alerts"
const val NOTIFICATION_ID_APP_DETECTED = 1001

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetectorScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var apps by remember { mutableStateOf<List<AppAnalysis>>(emptyList()) }
    var isScanning by remember { mutableStateOf(false) }
    var scanComplete by remember { mutableStateOf(false) }
    var selectedApp by remember { mutableStateOf<AppAnalysis?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf("all") }

    val bg = Color(0xFFF5F5F7)
    val white = Color.White
    val orange = Color(0xFFFF8C42)
    val orangeSoft = Color(0xFFFFE8D6)
    val blue = Color(0xFF4A90D9)
    val blueSoft = Color(0xFFD6EAFF)
    val red = Color(0xFFFF3B30)
    val redSoft = Color(0xFFFFD6D6)
    val green = Color(0xFF34C759)
    val greenSoft = Color(0xFFD4F5D4)
    val textDark = Color(0xFF1C1C1E)
    val textGray = Color(0xFF6E6E73)
    val cardBorder = Color(0xFFE5E5EA)

    // Criar canal de notificacao se necessario
    LaunchedEffect(Unit) {
        createNotificationChannel(context)
    }

    Scaffold(
        containerColor = bg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            //
            Surface(color = white, shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = textDark)
                    }
                    Spacer(Modifier.width(4.dp))
                    Column {
                        Text(
                            "Detector de Apps Suspeitas",
                            color = textDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Análise de segurança das aplicações instaladas",
                            color = textGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (!scanComplete) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(blueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Security, contentDescription = null, tint = blue, modifier = Modifier.size(48.dp))
                    }
                    Spacer(Modifier.height(20.dp))
                    Text("Análise de Aplicações", color = textDark, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "O Detector analisa:\n\n" +
                                "• Permissões de alto risco (SMS, Câmara, Localização, Pop-ups, Controlo do telemóvel)\n" +
                                "• Combinações perigosas (app que lê SMS e controla o ecrã ao mesmo tempo)\n" +
                                "• Incoerência: app de lanterna que pede acesso ao microfone\n" +
                                "• Origem: Play Store vs instalada manualmente (sideload)\n" +
                                "• Desenvolvedores conhecidos vs desconhecidos",
                        color = textGray,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(28.dp))
                    Button(
                        onClick = {
                            isScanning = true
                            scope.launch {
                                val result = scanInstalledAppsAdvanced(context)
                                apps = result
                                scanComplete = true
                                isScanning = false
                                // Notificar se encontrar apps suspeitas
                                val suspicious = result.filter { it.isSuspicious }
                                if (suspicious.isNotEmpty()) {
                                    sendAppNotification(context, suspicious)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = blue),
                        enabled = !isScanning
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("A analisar aplicações...", fontWeight = FontWeight.SemiBold)
                        } else {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Iniciar Análise", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    val suspiciousCount = apps.count { it.isSuspicious }
                    val totalCount = apps.size

                    Surface(color = white, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                SummaryItem("Total", "$totalCount", blue, blueSoft)
                                SummaryItem("Suspeitas", "$suspiciousCount", if (suspiciousCount > 0) red else green,
                                    if (suspiciousCount > 0) redSoft else greenSoft)
                                SummaryItem("Seguras", "${totalCount - suspiciousCount}", green, greenSoft)
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = filter == "all",
                                    onClick = { filter = "all" },
                                    label = { Text("Todas", fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = blue, selectedLabelColor = Color.White)
                                )
                                FilterChip(
                                    selected = filter == "suspicious",
                                    onClick = { filter = "suspicious" },
                                    label = { Text("Suspeitas", fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = red, selectedLabelColor = Color.White)
                                )
                                FilterChip(
                                    selected = filter == "safe",
                                    onClick = { filter = "safe" },
                                    label = { Text("Seguras", fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = green, selectedLabelColor = Color.White)
                                )
                            }
                        }
                    }

                    val filteredApps = when (filter) {
                        "suspicious" -> apps.filter { it.isSuspicious }
                        "safe" -> apps.filter { !it.isSuspicious }
                        else -> apps
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredApps) { app ->
                            AppCard(
                                app = app,
                                onClick = { selectedApp = app; showDetailDialog = true },
                                white = white, textDark = textDark, textGray = textGray,
                                green = green, red = red, orange = orange,
                                cardBorder = cardBorder, redSoft = redSoft,
                                orangeSoft = orangeSoft, greenSoft = greenSoft
                            )
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
            containerColor = white,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = if (app.isSuspicious) red else green, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(app.appName, fontWeight = FontWeight.Bold, color = textDark)
                }
            },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    DetailRow("App", app.appName, textGray, textDark)
                    DetailRow("Nome interno", app.packageName, textGray, textDark)
                    DetailRow("App do sistema", if (app.isSystemApp) "Sim, vem pré-instalada" else "Não, instalada por si", textGray, textDark)
                    DetailRow("Origem", if (app.isFromPlayStore) "Google Play Store ✓" else if (app.installerPackage == null && !app.isSystemApp) "Instalada manualmente (fonte externa) ✗" else if (app.isSystemApp) "Pré-instalada pelo fabricante" else "Fonte desconhecida", textGray, if (app.isFromPlayStore) green else red)
                    DetailRow("Nível de risco", "${app.riskScore}/100", textGray, if (app.riskScore >= 50) red else if (app.riskScore >= 25) orange else green)

                    //Seccao de permissoes
                    if (app.excessivePermissions.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Permissões de Alto Risco",
                            fontWeight = FontWeight.Bold,
                            color = red,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Estas permissões podem ser usadas para aceder a dados privados ou controlar o dispositivo:",
                            color = textGray, fontSize = 11.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        app.excessivePermissions.forEach { perm ->
                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                Icon(Icons.Filled.Warning, contentDescription = null, tint = red, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    PERMISSION_FRIENDLY_NAMES[perm] ?: perm,
                                    color = textDark,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    //Combinacoes perigosas
                    if (app.dangerousCombinations.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Combinações Perigosas",
                            fontWeight = FontWeight.Bold,
                            color = red,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "O perigo aumenta quando uma app junta várias permissões ao mesmo tempo:",
                            color = textGray, fontSize = 11.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        app.dangerousCombinations.forEach { combo ->
                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                Icon(Icons.Filled.Dangerous, contentDescription = null, tint = red, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(combo, color = textDark, fontSize = 13.sp)
                            }
                        }
                    }

                    //Incoerencias
                    if (app.permissionIncoherence.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Incoerências Suspeitas",
                            fontWeight = FontWeight.Bold,
                            color = orange,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "A app pede permissões que não fazem sentido para o que ela faz:",
                            color = textGray, fontSize = 11.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        app.permissionIncoherence.forEach { inc ->
                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                Icon(Icons.Filled.Circle, contentDescription = null, tint = orange, modifier = Modifier.size(6.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(inc, color = textDark, fontSize = 13.sp)
                            }
                        }
                    }

                    //Apps Legitimos
                    if (app.excessivePermissions.isNotEmpty() && app.isFromPlayStore && TRUSTED_DEVELOPERS.any { app.packageName.startsWith(it) }) {
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF0F8FF),  // azul muito claro
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Info, contentDescription = null, tint = blue, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "Nota sobre esta app",
                                        fontWeight = FontWeight.SemiBold,
                                        color = blue,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Esta aplicação foi instalada através da Google Play Store e pertence a um desenvolvedor de confiança. " +
                                            "Embora peça permissões de alto risco, estas são necessárias para o seu funcionamento normal " +
                                            "(ex: o Facebook precisa da câmara para tirar fotos, o WhatsApp precisa dos SMS para verificar o número). " +
                                            "Não há motivo imediato para preocupação, mas pode sempre rever as permissões nas Definições do seu telemóvel.",
                                    color = textDark,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    //  MOTIVOS DE SUSPEITA

                    if (app.reasons.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Por que razão esta app chama a atenção?",
                            fontWeight = FontWeight.Bold,
                            color = orange,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        app.reasons.forEach { reason ->
                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                Icon(Icons.Filled.Circle, contentDescription = null, tint = orange, modifier = Modifier.size(6.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(reason, color = textDark, fontSize = 13.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailDialog = false }) { Text("Fechar", color = blue, fontWeight = FontWeight.SemiBold) }
            }
        )
    }
}

@Composable
private fun SummaryItem(label: String, value: String, color: Color, bg: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
            Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, color = Color(0xFF6E6E73), fontSize = 11.sp)
    }
}

@Composable
private fun AppCard(
    app: AppAnalysis, onClick: () -> Unit,
    white: Color, textDark: Color, textGray: Color,
    green: Color, red: Color, orange: Color, cardBorder: Color,
    redSoft: Color, orangeSoft: Color, greenSoft: Color
) {
    val statusColor = when { app.riskScore >= 50 -> red; app.riskScore >= 25 -> orange; else -> green }
    val statusBg = when { app.riskScore >= 50 -> redSoft; app.riskScore >= 25 -> orangeSoft; else -> greenSoft }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = white),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(statusBg), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Android, contentDescription = null, tint = statusColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(app.appName, color = textDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                val details = buildString {
                    if (app.dangerousCombinations.isNotEmpty()) append("⚠ ${app.dangerousCombinations.size} combinação(ões) perigosa(s) • ")
                    append("${app.excessivePermissions.size} permissão(ões) de risco")
                }
                Text(details, color = textGray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = statusBg) {
                Text("${app.riskScore}", color = statusColor, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, textGray: Color, textValue: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = textGray, fontSize = 12.sp)
        Text(value, color = textValue, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private suspend fun scanInstalledAppsAdvanced(context: Context): List<AppAnalysis> = withContext(Dispatchers.IO) {
    val packageManager = context.packageManager
    val apps = mutableListOf<AppAnalysis>()

    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PackageManager.PackageInfoFlags.of((PackageManager.GET_PERMISSIONS or PackageManager.GET_SIGNING_CERTIFICATES).toLong())
    } else {
        @Suppress("DEPRECATION")
        PackageManager.GET_PERMISSIONS
    }

    val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getInstalledPackages(flags as PackageManager.PackageInfoFlags)
    } else {
        @Suppress("DEPRECATION")
        packageManager.getInstalledPackages(flags as Int)
    }

    for (pkg in packages) {
        val appInfo = pkg.applicationInfo ?: continue
        try {
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            if (isSystem) {
                val isTrusted = TRUSTED_SYSTEM_PACKAGES.any { pkg.packageName.startsWith(it) }
                if (isTrusted) continue
            }

            val appName = packageManager.getApplicationLabel(appInfo).toString()
            val installer = packageManager.getInstallerPackageName(pkg.packageName)
            val isFromPlayStore = installer == "com.android.vending" || installer == "com.google.android.feedback"
            val requestedPermissions = pkg.requestedPermissions?.toList() ?: emptyList()

            val excessivePerms = requestedPermissions.filter { it in HIGH_RISK_PERMISSIONS }.map { it.substringAfterLast(".") }.distinct()
            val permShortNames = excessivePerms.toSet()

            val dangerousCombos = DANGEROUS_COMBINATIONS.filter { (perms, _) -> perms.all { it in permShortNames } }.map { (_, desc) -> desc }

            val incoherences = mutableListOf<String>()
            val appCategory = getAppCategory(appName)
            val hasInternet = requestedPermissions.contains("android.permission.INTERNET")

            val isFlashlight = appName.lowercase().contains("flash") || appName.lowercase().contains("lantern") || appName.lowercase().contains("torch") || (appCategory == "TOOL" && appName.lowercase().contains("light"))
            if (isFlashlight) {
                if (permShortNames.contains("CAMERA")) incoherences.add("App de lanterna pede acesso à Câmara — não faz sentido")
                if (permShortNames.contains("READ_SMS")) incoherences.add("App de lanterna pede acesso a SMS — não faz sentido")
                if (permShortNames.contains("RECORD_AUDIO")) incoherences.add("App de lanterna pede acesso ao Microfone — não faz sentido")
                if (permShortNames.contains("ACCESS_FINE_LOCATION")) incoherences.add("App de lanterna pede a sua Localização — não faz sentido")
                if (permShortNames.contains("READ_CONTACTS")) incoherences.add("App de lanterna pede os seus Contactos — não faz sentido")
            }

            val isCalculator = appName.lowercase().contains("calculat") || (appCategory == "TOOL" && appName.lowercase().contains("calc"))
            if (isCalculator) {
                if (permShortNames.contains("CAMERA")) incoherences.add("Calculadora pede acesso à Câmara — não faz sentido")
                if (permShortNames.contains("READ_SMS")) incoherences.add("Calculadora pede acesso a SMS — não faz sentido")
                if (permShortNames.contains("RECORD_AUDIO")) incoherences.add("Calculadora pede acesso ao Microfone — não faz sentido")
            }

            val isWallpaper = appName.lowercase().contains("wallpaper") || appCategory == "PERSONALIZATION"
            if (isWallpaper) {
                if (permShortNames.contains("READ_SMS")) incoherences.add("App de wallpapers pede acesso a SMS — não faz sentido")
                if (permShortNames.contains("READ_CONTACTS")) incoherences.add("App de wallpapers pede os seus Contactos — não faz sentido")
            }

            var score = 0
            score += excessivePerms.size * 8
            score += dangerousCombos.size * 25
            score += incoherences.size * 20
            if (!isFromPlayStore && !isSystem) score += 15
            if (installer == null && !isSystem) score += 10
            if (appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) score += 25

            if (isFromPlayStore) score = (score - 15).coerceAtLeast(0)
            val categoryCompatible = isCategoryCompatible(appCategory, requestedPermissions)
            if (categoryCompatible) score = (score - 10).coerceAtLeast(0)
            if (TRUSTED_DEVELOPERS.any { pkg.packageName.startsWith(it) }) score = (score - 20).coerceAtLeast(0)

            score = score.coerceIn(0, 100)

            val isSuspicious = score >= 25 || dangerousCombos.isNotEmpty() || incoherences.isNotEmpty()

            val reasons = mutableListOf<String>()
            if (!isFromPlayStore && !isSystem) reasons.add("Foi instalada fora da Play Store (fonte externa)")
            if (installer == null && !isSystem) reasons.add("Não sabemos quem instalou esta app (sideload)")
            if (appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) reasons.add("App está em modo de desenvolvimento (debug)")
            if (dangerousCombos.isNotEmpty()) reasons.addAll(dangerousCombos)
            if (incoherences.isNotEmpty()) reasons.addAll(incoherences)

            apps.add(AppAnalysis(
                packageName = pkg.packageName, appName = appName, isSystemApp = isSystem,
                installerPackage = installer, isFromPlayStore = isFromPlayStore,
                hasExcessivePermissions = excessivePerms.isNotEmpty(), excessivePermissions = excessivePerms,
                dangerousCombinations = dangerousCombos, permissionIncoherence = incoherences,
                isSuspicious = isSuspicious, riskScore = score, reasons = reasons.distinct()
            ))
        } catch (_: Exception) { Log.e("AppDetector", "Error processing ${pkg.packageName}") }
    }
    apps.sortedByDescending { it.riskScore }
}

private fun getAppCategory(appName: String): String {
    return when {
        appName.contains("game", ignoreCase = true) || appName.contains("jogo", ignoreCase = true) -> "GAME"
        appName.contains("launcher", ignoreCase = true) || appName.contains("home", ignoreCase = true) -> "LAUNCHER"
        appName.contains("browser", ignoreCase = true) || appName.contains("navegador", ignoreCase = true) -> "BROWSER"
        appName.contains("camera", ignoreCase = true) || appName.contains("câmara", ignoreCase = true) -> "CAMERA"
        appName.contains("message", ignoreCase = true) || appName.contains("sms", ignoreCase = true) || appName.contains("chat", ignoreCase = true) -> "COMMUNICATION"
        appName.contains("social", ignoreCase = true) || appName.contains("facebook", ignoreCase = true) || appName.contains("instagram", ignoreCase = true) -> "SOCIAL"
        appName.contains("bank", ignoreCase = true) || appName.contains("banco", ignoreCase = true) -> "FINANCE"
        else -> "TOOL"
    }
}

private fun isCategoryCompatible(category: String, permissions: List<String>): Boolean {
    val permSet = permissions.toSet()
    return when (category) {
        "CAMERA" -> permSet.contains("android.permission.CAMERA")
        "COMMUNICATION" -> permSet.contains("android.permission.RECORD_AUDIO") || permSet.contains("android.permission.READ_CONTACTS") || permSet.contains("android.permission.CAMERA")
        "SOCIAL" -> permSet.contains("android.permission.CAMERA") || permSet.contains("android.permission.RECORD_AUDIO") || permSet.contains("android.permission.READ_CONTACTS")
        "FINANCE" -> true
        else -> false
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ALERTS, "Privacity - Alertas", NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Alertas de apps suspeitas e permissões" }
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }
}

private fun sendAppNotification(context: Context, suspicious: List<AppAnalysis>) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
    }
    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ALERTS)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("Apps Suspeitas Detectadas")
        .setContentText("${suspicious.size} app(s) suspeita(s) encontrada(s)")
        .setStyle(NotificationCompat.BigTextStyle().bigText(
            suspicious.joinToString("\n") { "• ${it.appName} (Score: ${it.riskScore})" }
        ))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_APP_DETECTED, notification)
}