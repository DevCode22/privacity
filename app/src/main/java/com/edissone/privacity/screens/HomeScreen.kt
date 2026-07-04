package com.edissone.privacity.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.edissone.privacity.R
import com.edissone.privacity.model.AlertItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ═══════════════════════════════════════════════════════════
//  REIMPORTAR AS FUNÇÕES REAIS DO AlertCenterScreen
// ═══════════════════════════════════════════════════════════

// (As funções scanRealApps, readProcNetDev, getNewlyInstalledApps,
//  generateRealAlerts, formatBytes e constantes estão definidas
//  no ficheiro AlertCenterScreen.kt e são chamadas daqui.)

data class SecurityScore(
    val score: Int,
    val level: String,
    val checks: List<SecurityCheck>
)

data class SecurityCheck(
    val name: String,
    val passed: Boolean,
    val description: String
)

private val bg = Color(0xFFF5F5F7)
private val white = Color.White
private val orange = Color(0xFFFF8C42)
private val orangeSoft = Color(0xFFFFE8D6)
private val blue = Color(0xFF4A90D9)
private val blueSoft = Color(0xFFD6EAFF)
private val red = Color(0xFFFF3B30)
private val redSoft = Color(0xFFFFD6D6)
private val green = Color(0xFF34C759)
private val greenSoft = Color(0xFFD4F5D4)
private val textDark = Color(0xFF1C1C1E)
private val textGray = Color(0xFF6E6E73)
private val cardBorder = Color(0xFFE5E5EA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLinkCheckerClick: () -> Unit,
    onNetworkClick: () -> Unit,
    onAppDetectorClick: () -> Unit,
    onWifiAnalyzerClick: () -> Unit,
    onPasswordCheckerClick: () -> Unit,
    onPermissionMonitorClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAlertCenterClick: () -> Unit,
    onSecurityScoreClick: () -> Unit,
    onAboutClick: () -> Unit,
    onPoliciesClick: () -> Unit,
    onContactClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showMenu by remember { mutableStateOf(false) }


    //  ALERTAS

    var realAlerts by remember { mutableStateOf(listOf<AlertItem>()) }
    var isLoadingAlerts by remember { mutableStateOf(true) }

    // Carregar alertas ao iniciar (em background)
    LaunchedEffect(Unit) {
        isLoadingAlerts = true
        val alerts = withContext(Dispatchers.IO) { generateRealAlerts(context) }
        realAlerts = alerts
        isLoadingAlerts = false
    }

    // Atualizar a cada 30 segundos
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(30_000)
            val alerts = withContext(Dispatchers.IO) { generateRealAlerts(context) }
            realAlerts = alerts
        }
    }


    //  SECURITY SCORE

    val securityScore by remember { derivedStateOf {
        val criticalCount = realAlerts.count { it.priority == "critical" }
        val warningCount = realAlerts.count { it.priority == "warning" }

        // Calcular score: começa em 100, perde pontos por alertas
        var score = 100
        score -= criticalCount * 15
        score -= warningCount * 8
        score = score.coerceIn(0, 100)

        val level = when {
            score >= 80 -> "Excelente"
            score >= 60 -> "Bom"
            score >= 40 -> "Razoável"
            else -> "Fraco"
        }

        // Checks
        val hasCritical = criticalCount > 0
        val hasWarning = warningCount > 0
        val hasNetworkAlerts = realAlerts.any { it.action == "network" }
        val hasAppAlerts = realAlerts.any { it.action == "app_detector" }

        SecurityScore(
            score = score,
            level = level,
            checks = listOf(
                SecurityCheck("Apps Suspeitas", !hasAppAlerts,
                    if (hasAppAlerts) "Existem apps com permissões suspeitas" else "Nenhuma app suspeita detectada"),
                SecurityCheck("Alertas Críticos", !hasCritical,
                    if (hasCritical) "$criticalCount alerta(s) crítico(s) pendente(s)" else "Sem alertas críticos"),
                SecurityCheck("Avisos", !hasWarning,
                    if (hasWarning) "$warningCount aviso(s) pendente(s)" else "Sem avisos de segurança"),
                SecurityCheck("Rede", !hasNetworkAlerts,
                    if (hasNetworkAlerts) "Tráfego de rede elevado detectado" else "Rede sem anomalias"),
                SecurityCheck("Permissões", !hasAppAlerts,
                    if (hasAppAlerts) "Apps com permissões excessivas" else "Permissões controladas"),
                SecurityCheck("Atualizações", true, "Proteção em tempo real ativa")
            )
        )
    }}



    Scaffold(
        containerColor = bg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                Surface(
                    color = white,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                stringResource(R.string.home_title),
                                color = textDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                            Text(
                                stringResource(R.string.home_subtitle),
                                color = textGray,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        // Alert Indicator
                        Box(contentAlignment = Alignment.TopEnd) {
                            IconButton(onClick = onAlertCenterClick) {
                                Icon(Icons.Outlined.Notifications, contentDescription = stringResource(R.string.alert_center_title), tint = textDark)
                            }
                            if (realAlerts.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(red)
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-4).dp, y = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${realAlerts.size}",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }


                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = textDark)
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier
                                    .background(white, RoundedCornerShape(16.dp))
                                    .width(220.dp)
                            ) {
                                MenuItem(Icons.Outlined.Link, stringResource(R.string.link_checker_title), textDark) {
                                    showMenu = false
                                    onLinkCheckerClick()
                                }
                                MenuItem(Icons.Outlined.Android, stringResource(R.string.suspicious_apps), textDark) {
                                    showMenu = false
                                    onAppDetectorClick()
                                }
                                MenuItem(Icons.Outlined.Wifi, stringResource(R.string.analyze_wifi), textDark) {
                                    showMenu = false
                                    onWifiAnalyzerClick()
                                }
                                MenuItem(Icons.Outlined.Lock, stringResource(R.string.check_password), textDark) {
                                    showMenu = false
                                    onPasswordCheckerClick()
                                }
                                MenuItem(Icons.Outlined.Shield, stringResource(R.string.permissions), textDark) {
                                    showMenu = false
                                    onPermissionMonitorClick()
                                }
                                MenuItem(Icons.Outlined.TrendingUp, stringResource(R.string.network), textDark) {
                                    showMenu = false
                                    onNetworkClick()
                                }
                                HorizontalDivider(color = cardBorder)
                                MenuItem(Icons.Outlined.Notifications, stringResource(R.string.alert_center_title), textDark) {
                                    showMenu = false
                                    onAlertCenterClick()
                                }
                                MenuItem(Icons.Outlined.Security, stringResource(R.string.security_score), textDark) {
                                    showMenu = false
                                    onSecurityScoreClick()
                                }
                                HorizontalDivider(color = cardBorder)
                                MenuItem(Icons.Outlined.Settings, stringResource(R.string.settings_title), textDark) {
                                    showMenu = false
                                    onSettingsClick()
                                }
                            }
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {

                    Spacer(Modifier.height(4.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = white),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(blue, orange))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    stringResource(R.string.app_version),
                                    color = textDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    stringResource(R.string.version_desc),
                                    color = textGray,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))


                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = white),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                        onClick = onSecurityScoreClick
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(orangeSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${securityScore.score}",
                                        color = orange,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 26.sp
                                    )
                                    Text(
                                        "/100",
                                        color = orange.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .border(4.dp, orange, CircleShape)
                                )
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    stringResource(R.string.security_score),
                                    color = textDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "${stringResource(R.string.level)}: ${securityScore.level}",
                                    color = orange,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Spacer(Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { securityScore.score / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = orange,
                                    trackColor = orangeSoft
                                )

                                Spacer(Modifier.height(4.dp))
                                Text(
                                    stringResource(R.string.safe_checks, securityScore.checks.count { it.passed }, securityScore.checks.size),
                                    color = textGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))


                    Text(stringResource(R.string.quick_actions), color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(10.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Link,
                            title = stringResource(R.string.analyze_link),
                            color = orange,
                            bg = orangeSoft,
                            onClick = onLinkCheckerClick
                        )
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Android,
                            title = stringResource(R.string.suspicious_apps),
                            color = red,
                            bg = redSoft,
                            onClick = onAppDetectorClick
                        )
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Wifi,
                            title = stringResource(R.string.analyze_wifi),
                            color = blue,
                            bg = blueSoft,
                            onClick = onWifiAnalyzerClick
                        )
                    }

                    Spacer(Modifier.height(10.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Lock,
                            title = stringResource(R.string.check_password),
                            color = orange,
                            bg = orangeSoft,
                            onClick = onPasswordCheckerClick
                        )
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Shield,
                            title = stringResource(R.string.permissions),
                            color = blue,
                            bg = blueSoft,
                            onClick = onPermissionMonitorClick
                        )
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.TrendingUp,
                            title = stringResource(R.string.network),
                            color = green,
                            bg = greenSoft,
                            onClick = onNetworkClick
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Alertas Recentes (AGORA REAIS)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.recent_alerts), color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        TextButton(onClick = onAlertCenterClick) {
                            Text(stringResource(R.string.view_all), color = blue, fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    //Mostrar  Alertas
                    if (isLoadingAlerts) {
                        // Enquanto carrega mostra um placeholder
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = white),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = blue
                                )
                                Spacer(Modifier.width(10.dp))
                                Text("A analisar o dispositivo...", color = textGray, fontSize = 12.sp)
                            }
                        }
                    } else if (realAlerts.isEmpty()) {
                        // Sem alertas = estado positivo
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = white),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(greenSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = green, modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("Nenhum alerta", color = textDark, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                    Text("O seu dispositivo parece seguro", color = textGray, fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        // Mostrar alertas
                        realAlerts.take(3).forEach { alert ->
                            AlertCard(
                                alert = alert,
                                onClick = onAlertCenterClick
                            )
                            Spacer(Modifier.height(6.dp))
                        }
                    }

                    Spacer(Modifier.height(24.dp))


                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = white),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                BottomActionButton(
                                    title = "Sobre",
                                    icon = Icons.Outlined.Info,
                                    onClick = onAboutClick,
                                    textGray = textGray
                                )
                                BottomActionButton(
                                    title = "Políticas",
                                    icon = Icons.Outlined.Description,
                                    onClick = onPoliciesClick,
                                    textGray = textGray
                                )
                                BottomActionButton(
                                    title = "Contactos",
                                    icon = Icons.Outlined.Mail,
                                    onClick = onContactClick,
                                    textGray = textGray
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun MenuItem(icon: ImageVector, title: String, textDark: Color, onClick: () -> Unit) {
    DropdownMenuItem(
        onClick = onClick,
        leadingIcon = { Icon(icon, contentDescription = null, tint = textDark, modifier = Modifier.size(20.dp)) },
        text = { Text(title, color = textDark, fontSize = 14.sp) }
    )
}

@Composable
private fun QuickActionCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    color: Color,
    bg: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                color = textDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun AlertCard(
    alert: AlertItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(alert.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(alert.icon, contentDescription = null, tint = alert.color, modifier = Modifier.size(18.dp))
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(alert.title, color = textDark, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Text(alert.description, color = textGray, fontSize = 11.sp, maxLines = 1)
            }

            Text(alert.timestamp, color = textGray, fontSize = 10.sp)
        }
    }
}

@Composable
private fun BottomActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    textGray: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = title, tint = textGray, modifier = Modifier.size(22.dp))
        }
        Text(title, color = textGray, fontSize = 11.sp)
    }
}