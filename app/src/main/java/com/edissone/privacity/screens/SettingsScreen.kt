package com.edissone.privacity.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
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
import com.edissone.privacity.service.PrivacityForegroundService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("privacity_prefs", Context.MODE_PRIVATE) }

    var backgroundScan by remember {
        mutableStateOf(prefs.getBoolean("background_scan_enabled", false))
    }
    var networkLimitText by remember {
        mutableStateOf(prefs.getString("network_limit_mb", "500") ?: "500")
    }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember {
        mutableStateOf(prefs.getString("app_language", "pt") ?: "pt")
    }

    val bg = Color(0xFFF5F5F7)
    val white = Color.White
    val blue = Color(0xFF4A90D9)
    val blueSoft = Color(0xFFD6EAFF)
    val orange = Color(0xFFFF8C42)
    val orangeSoft = Color(0xFFFFE8D6)
    val textDark = Color(0xFF1C1C1E)
    val textGray = Color(0xFF6E6E73)
    val cardBorder = Color(0xFFE5E5EA)
    val green = Color(0xFF34C759)
    val purple = Color(0xFF8E44AD)
    val purpleSoft = Color(0xFFF0E6FF)

    Scaffold(containerColor = bg) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Surface(color = white, shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = textDark)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("Configurações", color = textDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
            ) {
                // Secção: Monitorização
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Monitor, contentDescription = null, tint = blue, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Monitorização", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Análise em Segundo Plano", color = textDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text("Analisa apps novas e permissões automaticamente.", color = textGray, fontSize = 12.sp)
                            }
                            Switch(
                                checked = backgroundScan,
                                onCheckedChange = { enabled ->
                                    backgroundScan = enabled
                                    prefs.edit().putBoolean("background_scan_enabled", enabled).apply()
                                    if (enabled) {
                                        val intent = Intent(context, PrivacityForegroundService::class.java)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            context.startForegroundService(intent)
                                        } else {
                                            context.startService(intent)
                                        }
                                    } else {
                                        context.stopService(
                                            Intent(context, PrivacityForegroundService::class.java)
                                        )
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = blue,
                                    checkedThumbColor = Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Secção: Rede
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = orange, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Limite de Dados", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Notificar quando o uso de dados atingir:", color = textGray, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = networkLimitText,
                                onValueChange = { value ->
                                    if (value.all { it.isDigit() } && value.length <= 6) {
                                        networkLimitText = value
                                        prefs.edit().putString("network_limit_mb", value).apply()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                suffix = { Text("MB") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = orange,
                                    unfocusedBorderColor = cardBorder,
                                    cursorColor = orange
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Box(
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                                    .background(orangeSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.NotificationsActive,
                                    contentDescription = null,
                                    tint = orange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Secção: Novidades da Próxima Versão
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.NewReleases, contentDescription = null, tint = purple, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Próxima Atualização", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = purpleSoft
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Estamos a preparar novidades importantes para a próxima versão!",
                                    color = purple,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Detecao de RATs
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp))
                                    .background(redSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Smartphone,
                                    contentDescription = null,
                                    tint = Color(0xFFFF3B30),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Detetor de RATs", color = textDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text(
                                    "Deteção de Remote Access Trojans — apps que permitem acesso remoto ao seu dispositivo sem o seu conhecimento.",
                                    color = textGray, fontSize = 12.sp, lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Malware Android
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFD6D6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.BugReport,
                                    contentDescription = null,
                                    tint = Color(0xFFFF3B30),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Base de Malware Conhecido", color = textDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text(
                                    "Análise contra uma base de dados de malwares Android conhecidos, incluindo banking trojans, spyware e adware.",
                                    color = textGray, fontSize = 12.sp, lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Phishing
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFE8D6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Link,
                                    contentDescription = null,
                                    tint = Color(0xFFFF8C42),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Proteção Anti-Phishing", color = textDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text(
                                    "Bloqueio de tentativas de phishing em tempo real — sites e apps que tentam roubar as suas credenciais.",
                                    color = textGray, fontSize = 12.sp, lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Hijackers
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFE8D6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.SwapHoriz,
                                    contentDescription = null,
                                    tint = Color(0xFFFF8C42),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Deteção de Hijackers", color = textDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text(
                                    "Identificação de apps que sequestram o seu navegador, alteram definições ou redirecionam para sites maliciosos.",
                                    color = textGray, fontSize = 12.sp, lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Mais
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp))
                                    .background(blueSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = blue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("E muito mais...", color = textDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text(
                                    "Melhorias no motor de análise, deteção de keyloggers, monitorização de permissões em tempo real e uma interface renovada.",
                                    color = textGray, fontSize = 12.sp, lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Seccao: Idioma
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Language, contentDescription = null, tint = blue, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Idioma", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Idioma da aplicação", color = textDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text(
                                    when (selectedLanguage) {
                                        "pt" -> "Português"
                                        "en" -> "English"
                                        "ru" -> "Русский"
                                        else -> "Português"
                                    },
                                    color = textGray, fontSize = 12.sp
                                )
                            }
                            OutlinedButton(
                                onClick = { showLanguageDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, blue.copy(alpha = 0.5f))
                            ) {
                                Text("Brevemente", color = blue, fontSize = 13.sp)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "A mudança de idioma estará disponível numa próxima atualização.",
                            color = textGray, fontSize = 11.sp
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Seccao: Sobre
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = green, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Informação", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Privacity v1.0.0", color = textDark, fontSize = 14.sp)
                        Text("Proteja a sua privacidade digital.", color = textGray, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }

    if (showLanguageDialog) {
        val languages = listOf(
            Triple("pt", "Português", "🇵🇹"),
            Triple("en", "English", "🇬🇧"),
            Triple("ru", "Русский", "🇷🇺")
        )

        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = white,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Selecionar Idioma", fontWeight = FontWeight.Bold, color = textDark) },
            text = {
                Column {
                    Text(
                        "A funcionalidade de mudança de idioma será ativada numa próxima atualização. Fique atento!",
                        color = textGray, fontSize = 13.sp, lineHeight = 18.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    languages.forEach { (code, name, flag) ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "$flag  $name",
                                color = textGray.copy(alpha = 0.6f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(Modifier.weight(1f))
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = null,
                                tint = textGray.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (code != languages.last().first) {
                            HorizontalDivider(color = cardBorder)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Fechar", color = blue, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}


private val redSoft = Color(0xFFFFD6D6)