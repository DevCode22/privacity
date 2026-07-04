package com.edissone.privacity.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScoreScreen(onBack: () -> Unit = {}) {
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


    val score = 72
    val checks = listOf(
        SecurityCheckItem("VPN Ativa", false, red, "VPN não está ativa. Use uma VPN em redes públicas.", Icons.Outlined.VpnLock),
        SecurityCheckItem("Apps Suspeitas", true, green, "Nenhuma aplicação suspeita detectada.", Icons.Outlined.Android),
        SecurityCheckItem("Rede Wi-Fi Segura", false, red, "Conectado a rede pública aberta.", Icons.Outlined.Wifi),
        SecurityCheckItem("Senhas Fortes", true, green, "Senhas configuradas com nível bom ou superior.", Icons.Outlined.Lock),
        SecurityCheckItem("Permissões Controladas", true, green, "Apps com permissões dentro do normal.", Icons.Outlined.Shield),
        SecurityCheckItem("Sistema Atualizado", false, red, "Patch de segurança de Setembro de 2024 pendente.", Icons.Outlined.SystemUpdate),
        SecurityCheckItem("HTTPS em Uso", true, green, "Navegação maioritariamente por HTTPS.", Icons.Outlined.Https),
        SecurityCheckItem("2FA/MFA Configurado", true, green, "Autenticação de dois fatores ativa em contas críticas.", Icons.Outlined.Fingerprint),
        SecurityCheckItem("Apps de Fontes Confiáveis", true, green, "Apenas apps da Play Store instaladas.", Icons.Outlined.Store),
        SecurityCheckItem("Dados Biométricos", false, orange, "Impressão digital configurada mas sem PIN de reserva forte.", Icons.Outlined.Face)
    )

    val passedCount = checks.count { it.passed }
    val failedCount = checks.size - passedCount

    Scaffold(
        containerColor = bg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

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
                    Text("Score de Segurança", color = textDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.sweepGradient(
                                        listOf(orange, blue, green, orange)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(white),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "$score",
                                        color = textDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 44.sp
                                    )
                                    Text(
                                        "/ 100",
                                        color = textGray,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        Text(
                            when {
                                score >= 85 -> "Excelente"
                                score >= 70 -> "Bom"
                                score >= 50 -> "Razoável"
                                score >= 30 -> "Fraco"
                                else -> "Crítico"
                            },
                            color = when {
                                score >= 85 -> green
                                score >= 70 -> blue
                                score >= 50 -> orange
                                else -> red
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "O seu dispositivo tem um nível de privacidade considerado bom. " +
                                    "Melhore os itens abaixo para aumentar a sua pontuação.",
                            color = textGray,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(16.dp))


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$passedCount", color = green, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                Text("Seguras", color = textGray, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$failedCount", color = red, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                Text("Melhorar", color = textGray, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${checks.size}", color = blue, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                Text("Total", color = textGray, fontSize = 12.sp)
                            }
                        }

                        Spacer(Modifier.height(16.dp))


                        LinearProgressIndicator(
                            progress = { score / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = orange,
                            trackColor = orangeSoft
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))


                Text("Verificações de Segurança", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(10.dp))

                checks.forEach { check ->
                    SecurityCheckCard(
                        check = check,
                        white = white,
                        textDark = textDark,
                        textGray = textGray,
                        cardBorder = cardBorder,
                        green = green,
                        greenSoft = greenSoft,
                        red = red,
                        redSoft = redSoft,
                        orange = orange,
                        orangeSoft = orangeSoft
                    )
                    Spacer(Modifier.height(6.dp))
                }

                Spacer(Modifier.height(16.dp))


                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = orange, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Dicas para Melhorar", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("• Ative uma VPN quando usar redes Wi-Fi públicas", color = textDark, fontSize = 12.sp)
                        Text("• Mantenha o sistema e apps sempre atualizados", color = textDark, fontSize = 12.sp)
                        Text("• Reveja as permissões das apps regularmente", color = textDark, fontSize = 12.sp)
                        Text("• Use autenticação de dois fatores (2FA)", color = textDark, fontSize = 12.sp)
                        Text("• Evite instalar apps de fontes desconhecidas", color = textDark, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

data class SecurityCheckItem(
    val title: String,
    val passed: Boolean,
    val color: Color,
    val description: String,
    val icon: ImageVector
)

@Composable
private fun SecurityCheckCard(
    check: SecurityCheckItem,
    white: Color,
    textDark: Color,
    textGray: Color,
    cardBorder: Color,
    green: Color,
    greenSoft: Color,
    red: Color,
    redSoft: Color,
    orange: Color,
    orangeSoft: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.3.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (check.passed) greenSoft
                        else if (check.color == orange) orangeSoft
                        else redSoft
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (check.passed) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                    contentDescription = null,
                    tint = if (check.passed) green else check.color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(check.title, color = textDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Spacer(Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (check.passed) greenSoft else redSoft
                    ) {
                        Text(
                            if (check.passed) "OK" else "X",
                            color = if (check.passed) green else check.color,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(check.description, color = textGray, fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
    }
}