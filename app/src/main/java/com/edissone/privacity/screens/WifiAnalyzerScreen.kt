package com.edissone.privacity.screens

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class WiFiNetwork(
    val ssid: String,
    val bssid: String,
    val level: Int,
    val frequency: Int,
    val capabilities: String,
    val isOpen: Boolean,
    val encryptionType: String,
    val isSuspiciousName: Boolean,
    val securityScore: Int,
    val securityIssues: List<String>
)

data class CurrentConnection(
    val ssid: String,
    val bssid: String,
    val isPublic: Boolean,
    val encryptionType: String,
    val signalStrength: Int,
    val ipAddress: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiAnalyzerScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var networks by remember { mutableStateOf<List<WiFiNetwork>>(emptyList()) }
    var currentConnection by remember { mutableStateOf<CurrentConnection?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var scanComplete by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }
    var showPublicNetworkAlert by remember { mutableStateOf(false) }
    var selectedNetwork by remember { mutableStateOf<WiFiNetwork?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var wifiEnabled by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var noResultsFound by remember { mutableStateOf(false) }

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

    val suspiciousNames = listOf(
        "free_wifi", "free internet", "free wifi", "public wifi", "grátis", "gratis",
        "wi-fi grátis", "wifi_free", "rede_aberta", "internet_gratis",
        "starbucks", "mcdonalds", "vodafone", "meo", "nos", "altice"
    )

    // ─────────────────────────────────────────────────────
    //  FUNCAO PARA EXECUTAR O SCAN
    // ─────────────────────────────────────────────────────
    fun performWifiScan() {
        isScanning = true
        errorMessage = null
        noResultsFound = false
        scope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    scanWifiNetworks(context, suspiciousNames)
                }
                networks = result.networks
                currentConnection = result.currentConnection
                wifiEnabled = result.wifiEnabled
                scanComplete = true
                isScanning = false
                noResultsFound = result.networks.isEmpty()

                // Alerta se conectado a rede publica
                if (result.currentConnection?.isPublic == true) {
                    showPublicNetworkAlert = true
                }
            } catch (e: Exception) {
                isScanning = false
                scanComplete = true
                errorMessage = "Erro ao analisar redes: ${e.localizedMessage ?: "desconhecido"}"
            }
        }
    }

    // ─────────────────────────────────────────────────────
    //  PEDIR PERMISSAO DE LOCALIZACAO
    // ─────────────────────────────────────────────────────
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (granted) {
            // Permissao concedida → iniciar scan automaticamente
            performWifiScan()
        } else {
            errorMessage = "A permissão de localização é necessária para analisar redes Wi-Fi"
        }
    }

    // ─────────────────────────────────────────────────────
    //  VERIFICAR PERMISSÃO AO INICIAR
    // ─────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        if (hasPermission) {
            permissionGranted = true
            // Já tem permissao → fazer scan automaticamente
            performWifiScan()
        } else {
            // Não tem permissao → pedir
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }



    Scaffold(
        containerColor = bg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Surface(
                color = white,
                shadowElevation = 1.dp
            ) {
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
                            "Analisador de Wi-Fi",
                            color = textDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Redes, encriptação e segurança",
                            color = textGray,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    // Botao de atualizar
                    if (scanComplete && !isScanning) {
                        IconButton(onClick = { performWifiScan() }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Atualizar", tint = blue)
                        }
                    }
                }
            }

            // ── PERMISSAO NEGADA ──
            if (!permissionGranted) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.LocationOff, contentDescription = null, tint = orange, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Permissão de Localização Necessária", color = textDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Para analisar redes Wi-Fi, o Android exige a permissão de localização.\n\n" +
                                "Apenas usamos esta permissão para listar as redes disponíveis. " +
                                "A sua localização não é guardada nem partilhada.",
                        color = textGray, fontSize = 14.sp, lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                        colors = ButtonDefaults.buttonColors(containerColor = blue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Conceder Permissão", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            //  A SCANEAR ESTADO INICIAL
            else if (isScanning) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = blue, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("A analisar redes Wi-Fi...", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("A procurar redes disponíveis nas proximidades", color = textGray, fontSize = 13.sp)
                }
            }

            // ── WI-FI DESLIGADO ──
            else if (scanComplete && !wifiEnabled) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(100.dp).clip(CircleShape).background(orangeSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.WifiOff, contentDescription = null, tint = orange, modifier = Modifier.size(48.dp))
                    }
                    Spacer(Modifier.height(20.dp))
                    Text("Wi-Fi Desligado", color = textDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Ligue o Wi-Fi nas definições do seu telemóvel para analisar as redes disponíveis.",
                        color = textGray, fontSize = 14.sp, lineHeight = 20.sp, textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { performWifiScan() },
                        colors = ButtonDefaults.buttonColors(containerColor = blue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Tentar Novamente", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ── ERRO ──
            else if (errorMessage != null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = red, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Ocorreu um Erro", color = textDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(errorMessage ?: "", color = textGray, fontSize = 14.sp, lineHeight = 20.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { performWifiScan() },
                        colors = ButtonDefaults.buttonColors(containerColor = blue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Tentar Novamente", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ── SEM RESULTADOS ──
            else if (noResultsFound) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(100.dp).clip(CircleShape).background(blueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.WifiFind, contentDescription = null, tint = blue, modifier = Modifier.size(48.dp))
                    }
                    Spacer(Modifier.height(20.dp))
                    Text("Nenhuma Rede Encontrada", color = textDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Não foram encontradas redes Wi-Fi nas proximidades.\n" +
                                "Tente aproximar-se de um router ou verifique se o Wi-Fi está ligado.",
                        color = textGray, fontSize = 14.sp, lineHeight = 20.sp, textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { performWifiScan() },
                        colors = ButtonDefaults.buttonColors(containerColor = blue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Procurar Novamente", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ── RESULTADOS DO SCAN ──
            else if (scanComplete) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // Conexão atual
                    currentConnection?.let { conn ->
                        Surface(color = white, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (conn.isPublic) Icons.Filled.Warning else Icons.Filled.Wifi,
                                        contentDescription = null,
                                        tint = if (conn.isPublic) red else blue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Ligado a: ", color = textGray, fontSize = 13.sp)
                                    Text(conn.ssid, color = textDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                if (conn.isPublic) {
                                    Spacer(Modifier.height(6.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = redSoft),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Shield, contentDescription = null, tint = red, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Rede pública - os seus dados podem estar expostos", color = red, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }

                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    InfoChip("Sinal", "${conn.signalStrength}%", blue, blueSoft)
                                    InfoChip("Encriptação", conn.encryptionType, if (conn.encryptionType == "Aberta") red else green,
                                        if (conn.encryptionType == "Aberta") redSoft else greenSoft)
                                }
                            }
                        }
                    }

                    // Lista de redes
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                "Redes Disponíveis (${networks.size})",
                                color = textDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        items(networks) { network ->
                            WifiNetworkCard(
                                network = network,
                                onClick = {
                                    selectedNetwork = network
                                    showDetailDialog = true
                                },
                                white = white,
                                textDark = textDark,
                                textGray = textGray,
                                green = green,
                                red = red,
                                orange = orange,
                                blue = blue,
                                cardBorder = cardBorder,
                                redSoft = redSoft,
                                orangeSoft = orangeSoft,
                                greenSoft = greenSoft
                            )
                        }
                    }
                }
            }

            // ── ESTADO INICIAL (nunca fez scan, a aguardar) ──
            else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(100.dp).clip(CircleShape).background(blueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Wifi, contentDescription = null, tint = blue, modifier = Modifier.size(48.dp))
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        "Analisador de Redes Wi-Fi",
                        color = textDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Este analisador verifica:\n\n" +
                                "• Redes abertas sem palavra-passe\n" +
                                "• Tipo de encriptação (WPA2, WPA3, etc.)\n" +
                                "• Nomes suspeitos de redes\n" +
                                "• Alerta ao conectar-se a redes públicas",
                        color = textGray,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(28.dp))

                    Button(
                        onClick = { performWifiScan() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = blue),
                        enabled = !isScanning
                    ) {
                        Icon(Icons.Filled.WifiFind, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Analisar Redes", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // ── DIALOG: REDE PÚBLICA ──
    if (showPublicNetworkAlert) {
        AlertDialog(
            onDismissRequest = { showPublicNetworkAlert = false },
            containerColor = white,
            shape = RoundedCornerShape(24.dp),
            icon = {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = red, modifier = Modifier.size(36.dp))
            },
            title = {
                Text("Rede Pública Detectada", fontWeight = FontWeight.Bold, color = textDark)
            },
            text = {
                Text(
                    "Está conectado a uma rede Wi-Fi pública ou aberta.\n\n" +
                            "Recomendações:\n• Use uma VPN\n• Evite aceder a bancos ou sites sensíveis\n• Confirme que os sites usam HTTPS",
                    color = textGray,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showPublicNetworkAlert = false }) {
                    Text("Entendi", color = blue, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    // ── DIALOG: DETALHES DA REDE ──
    if (showDetailDialog && selectedNetwork != null) {
        val net = selectedNetwork!!
        AlertDialog(
            onDismissRequest = { showDetailDialog = false },
            containerColor = white,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (net.isOpen) Icons.Filled.Warning else Icons.Filled.Wifi,
                        contentDescription = null,
                        tint = if (net.isOpen) red else green,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(net.ssid.ifEmpty { "Rede Oculta" }, fontWeight = FontWeight.Bold, color = textDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            },
            text = {
                Column {
                    DetailRow("BSSID", net.bssid.ifEmpty { "N/A" }, textGray, textDark)
                    DetailRow("Sinal", "${net.level} dBm", textGray, textDark)
                    DetailRow("Frequência", "${net.frequency} MHz", textGray, textDark)
                    DetailRow("Encriptação", net.encryptionType, textGray,
                        if (net.encryptionType == "Aberta") red else green)
                    DetailRow("Nome Suspeito", if (net.isSuspiciousName) "Sim — pode ser uma rede falsa" else "Não", textGray,
                        if (net.isSuspiciousName) red else green)
                    DetailRow("Score Segurança", "${net.securityScore}/100", textGray,
                        if (net.securityScore < 50) red else if (net.securityScore < 80) orange else green)

                    if (net.securityIssues.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text("Problemas de Segurança", fontWeight = FontWeight.Bold, color = red, fontSize = 14.sp)
                        net.securityIssues.forEach { issue ->
                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Filled.Circle, contentDescription = null, tint = red, modifier = Modifier.size(6.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(issue, color = textDark, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailDialog = false }) {
                    Text("Fechar", color = blue, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

// ═══════════════════════════════════════════════════════════
//  COMPONENTES UI (inalterados)
// ═══════════════════════════════════════════════════════════

@Composable
private fun DetailRow(label: String, value: String, textGray: Color, textValue: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = textGray, fontSize = 13.sp)
        Text(value, color = textValue, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}

@Composable
private fun InfoChip(label: String, value: String, color: Color, bg: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(8.dp).clip(CircleShape).background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text("$label: ", color = Color(0xFF6E6E73), fontSize = 11.sp)
        Text(value, color = color, fontWeight = FontWeight.Medium, fontSize = 11.sp)
    }
}

@Composable
private fun WifiNetworkCard(
    network: WiFiNetwork,
    onClick: () -> Unit,
    white: Color,
    textDark: Color,
    textGray: Color,
    green: Color,
    red: Color,
    orange: Color,
    blue: Color,
    cardBorder: Color,
    redSoft: Color,
    orangeSoft: Color,
    greenSoft: Color
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = white),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(
                    when {
                        network.isOpen -> redSoft
                        network.isSuspiciousName -> orangeSoft
                        else -> greenSoft
                    }
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Wifi,
                    contentDescription = null,
                    tint = when {
                        network.isOpen -> red
                        network.isSuspiciousName -> orange
                        else -> green
                    },
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    network.ssid.ifEmpty { "Rede Oculta" },
                    color = textDark,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${network.encryptionType} • ${network.level} dBm",
                    color = textGray,
                    fontSize = 11.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when {
                    network.securityScore < 40 -> redSoft
                    network.securityScore < 70 -> orangeSoft
                    else -> greenSoft
                }
            ) {
                Text(
                    if (network.isOpen) "ABERTA" else "${network.securityScore}",
                    color = when {
                        network.securityScore < 40 -> red
                        network.securityScore < 70 -> orange
                        else -> green
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
//  SCAN REAL (reescrito para funcionar corretamente)
// ═══════════════════════════════════════════════════════════

private data class ScanResult(
    val networks: List<WiFiNetwork>,
    val currentConnection: CurrentConnection?,
    val wifiEnabled: Boolean
)

private fun scanWifiNetworks(
    context: Context,
    suspiciousNames: List<String>
): ScanResult {
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkList = mutableListOf<WiFiNetwork>()

    // Verificar se Wi-Fi está ligado
    if (!wifiManager.isWifiEnabled) {
        return ScanResult(emptyList(), null, false)
    }

    // ── CONEXÃO ATUAL ──
    var currentConnection: CurrentConnection? = null
    try {
        val network = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(network)
        val wifiInfo = wifiManager.connectionInfo

        if (wifiInfo != null) {
            val ssid = wifiInfo.ssid?.removeSurrounding("\"") ?: "Desconhecida"
            val bssid = wifiInfo.bssid ?: ""
            val rssi = wifiInfo.rssi
            val ipInt = wifiInfo.ipAddress
            val ipStr = if (ipInt != 0) {
                "${ipInt and 0xFF}.${(ipInt shr 8) and 0xFF}.${(ipInt shr 16) and 0xFF}.${(ipInt shr 24) and 0xFF}"
            } else {
                "N/A"
            }

            // Determinar encriptação e se é pública
            var encType = "Desconhecida"
            var isOpen = false

            try {
                val scanResults = wifiManager.scanResults
                scanResults.forEach { result ->
                    if (result.SSID == ssid || result.BSSID == bssid) {
                        encType = when {
                            result.capabilities.contains("WPA3-SAE") -> "WPA3"
                            result.capabilities.contains("WPA3") -> "WPA3"
                            result.capabilities.contains("WPA2") -> "WPA2"
                            result.capabilities.contains("WPA") -> "WPA"
                            result.capabilities.contains("WEP") -> "WEP"
                            result.capabilities.contains("[ESS]") -> "Aberta"
                            else -> "Desconhecida"
                        }
                        isOpen = encType == "Aberta"
                    }
                }
            } catch (_: Exception) {}

            currentConnection = CurrentConnection(
                ssid = ssid,
                bssid = bssid,
                isPublic = isOpen || suspiciousNames.any { ssid.lowercase().contains(it) },
                encryptionType = encType,
                signalStrength = (rssi + 100).coerceIn(0, 100),
                ipAddress = ipStr
            )
        }
    } catch (_: Exception) {}

    // ── LER REDES DISPONÍVEIS ──
    try {
        // Iniciar scan (resultados frescos)
        val scanStarted = wifiManager.startScan()

        // Pequena pausa para o scan completar
        if (scanStarted) {
            Thread.sleep(2000)
        }

        val scanResults = wifiManager.scanResults

        scanResults.forEach { result ->
            val ssid = result.SSID.ifEmpty { "Rede Oculta" }
            val encryptionType = when {
                result.capabilities.contains("WPA3-SAE") -> "WPA3"
                result.capabilities.contains("WPA3") -> "WPA3"
                result.capabilities.contains("WPA2") -> "WPA2"
                result.capabilities.contains("WPA") -> "WPA"
                result.capabilities.contains("WEP") -> "WEP"
                result.capabilities.contains("[ESS]") -> "Aberta"
                else -> "Desconhecida"
            }
            val isOpen = encryptionType == "Aberta"

            val isSuspicious = suspiciousNames.any { name ->
                ssid.lowercase().contains(name.lowercase())
            }

            val issues = mutableListOf<String>()
            var score = 100

            if (isOpen) {
                issues.add("Rede sem palavra-passe")
                score -= 50
            }

            if (encryptionType == "WEP") {
                issues.add("Encriptação WEP obsoleta e insegura")
                score -= 30
            }

            if (isSuspicious) {
                issues.add("Nome suspeito — pode ser uma rede falsa (honeypot)")
                score -= 25
            }

            if (result.level < -80) {
                issues.add("Sinal fraco")
                score -= 5
            }

            if (encryptionType == "WPA3") {
                score += 10
            }

            score = score.coerceIn(0, 100)

            networkList.add(
                WiFiNetwork(
                    ssid = ssid,
                    bssid = result.BSSID,
                    level = result.level,
                    frequency = result.frequency,
                    capabilities = result.capabilities,
                    isOpen = isOpen,
                    encryptionType = encryptionType,
                    isSuspiciousName = isSuspicious,
                    securityScore = score,
                    securityIssues = issues
                )
            )
        }
    } catch (_: Exception) {}

    return ScanResult(
        networks = networkList.sortedByDescending { it.level },
        currentConnection = currentConnection,
        wifiEnabled = true
    )
}