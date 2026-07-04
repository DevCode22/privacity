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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val bg = Color(0xFFF5F5F7)
private val white = Color.White
private val orange = Color(0xFFFF8C42)
private val orangeSoft = Color(0xFFFFE8D6)
private val blue = Color(0xFF4A90D9)
private val blueSoft = Color(0xFFD6EAFF)
private val green = Color(0xFF34C759)
private val textDark = Color(0xFF1C1C1E)
private val textGray = Color(0xFF6E6E73)
private val cardBorder = Color(0xFFE5E5EA)
private val greenSoft = Color(0xFFD4F5D4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit = {}) {
    Scaffold(
        containerColor = bg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
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
                    Text("Sobre", color = textDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                //
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(blue, orange))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Privacity",
                            color = textDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )

                        Text(
                            "v1.0.0",
                            color = textGray,
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "A sua privacidade digital protegida.",
                            color = textGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Cartao da Descricao
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = blue, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Sobre esta Aplicação", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "O Privacity é uma aplicação completa de segurança e privacidade para Android, " +
                                    "desenvolvida para o ajudar a monitorizar e proteger os seus dados digitais.",
                            color = textDark,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            "Com ferramentas integradas de análise de redes Wi-Fi, deteção de aplicações " +
                                    "suspeitas, verificação de links para phishing, monitorização de permissões e " +
                                    "muito mais, o Privacity coloca o controlo da sua privacidade nas suas mãos.",
                            color = textDark,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            "Este é um projeto de código aberto (código-fonte disponível no GitHub), " +
                                    "construído com foco em transparência e confiança.",
                            color = textGray,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))


                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Star, contentDescription = null, tint = orange, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Funcionalidades", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(Modifier.height(12.dp))

                        FeatureItem("Analisador de Links", "Deteção avançada de phishing em túneis, CDNs e domínios suspeitos", blue, blueSoft)
                        FeatureItem("Detector de Apps", "Identifica aplicações com permissões excessivas ou comportamento suspeito", orange, orangeSoft)
                        FeatureItem("Analisador Wi-Fi", "Escaneia redes próximas, deteta redes abertas e avalia a segurança", blue, blueSoft)
                        FeatureItem("Verificador de Senhas", "Avalia a força das suas senhas com métricas profissionais", orange, orangeSoft)
                        FeatureItem("Monitor de Permissões", "Acompanha as permissões concedidas a cada aplicação", blue, blueSoft)
                        FeatureItem("Uso de Rede", "Monitoriza o consumo de dados em tempo real por períodos", green, greenSoft)
                        FeatureItem("Centro de Alertas", "Notificações sobre Wi-Fi pública, apps instaladas e links suspeitos", green, greenSoft)
                        FeatureItem("Score de Segurança", "Avaliação geral da segurança do seu dispositivo", orange, orangeSoft)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Card de Informacoes
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Code, contentDescription = null, tint = blue, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Informações Técnicas", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(Modifier.height(12.dp))

                        TechInfoRow("Versão", "1.0.0")
                        TechInfoRow("SDK ", "36 (Android 16)")
                        TechInfoRow("SDK Mínimo", "26 (Android 7.0)")
                        TechInfoRow("Linguagem", "Kotlin + Jetpack Compose")
                        TechInfoRow("Arquitetura", "Material Design 3")
                        TechInfoRow("Package", "com.edissone.privacity")
                        TechInfoRow("Licença", "MIT - Código Aberto")
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Cartao developer
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = orange, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Desenvolvido por", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "DevCode",
                            color = textDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Text(
                            "Desenvolvedor Android Junior e em segurança digital, privacidade e " +
                                    "desenvolvimento mobile com foco em proteger os dados dos utilizadores.",
                            color = textGray,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun FeatureItem(title: String, description: String, iconColor: Color, bgColor: Color) {
    Row(
        modifier = Modifier.padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, color = textDark, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            Text(description, color = textGray, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun TechInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = textGray, fontSize = 13.sp)
        Text(value, color = textDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}