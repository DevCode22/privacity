package com.edissone.privacity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edissone.privacity.screens.*
import com.edissone.privacity.ui.theme.PrivacityTheme
import kotlinx.coroutines.delay
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PrivacityTheme {
                var mostrarSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(3500)
                    mostrarSplash = false
                }

                if (mostrarSplash) {
                    EcrãSplash()
                } else {
                    ConteudoPrincipal()
                }
            }
        }
    }
}

@Composable
fun EcrãSplash() {
    val textoEscuro = Color(0xFF1C1C1E)
    val textoCinza = Color(0xFF6E6E73)
    val azul = Color(0xFF4A90D9)
    val azulClaro = Color(0xFFD6EAFF)
    val laranja = Color(0xFFFF8C42)
    val verde = Color(0xFF34C759)

    //Animacoes
    val animacaoInfinita = rememberInfiniteTransition(label = "splash")


    val rotacao by animacaoInfinita.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotacao"
    )

    // Brilho
    val brilho by animacaoInfinita.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "brilho"
    )


    val ondaProgress by animacaoInfinita.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "onda"
    )


    val opacidadeTexto by animacaoInfinita.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "opacidadeTexto"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Spacer(Modifier.weight(0.4f))


            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centro = Offset(size.width / 2f, size.height / 2f)
                    val raio = size.minDimension / 2f


                    drawCircle(
                        color = azul.copy(alpha = brilho * 0.15f),
                        radius = raio * 1.1f
                    )
                    drawCircle(
                        color = azul.copy(alpha = brilho * 0.1f),
                        radius = raio * 1.2f
                    )
                }


                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .rotate(rotacao),
                    tint = azul
                )


                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centro = Offset(size.width / 2f, size.height / 2f)
                    val angulo = ondaProgress * 360f
                    val radianos = Math.toRadians(angulo.toDouble())
                    val raioOnda = size.minDimension / 2f * 0.7f

                    val x = centro.x + (raioOnda * cos(radianos)).toFloat()
                    val y = centro.y + (raioOnda * sin(radianos)).toFloat()

                    drawCircle(
                        color = Color.White,
                        radius = 6f,
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = azul.copy(alpha = 0.6f),
                        radius = 10f,
                        center = Offset(x, y)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Titulo
            Text(
                "Privacity",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = textoEscuro,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Proteja a sua privacidade digital.",
                color = textoCinza,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(48.dp))


            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    "Análise\nde Apps" to Icons.Outlined.Security,
                    "Proteção\nde Rede" to Icons.Filled.Shield,
                    "Monitorização\nde Permissões" to Icons.Outlined.Security
                ).forEach { (texto, icone) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icone,
                                contentDescription = null,
                                tint = azul,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                texto,
                                fontSize = 10.sp,
                                color = textoCinza,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(0.6f))


            Text(
                "A proteger o seu dispositivo...",
                color = azul.copy(alpha = opacidadeTexto),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Versão 1.0.0",
                color = textoCinza.copy(alpha = 0.5f),
                fontSize = 11.sp
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

// Conteudo Principal

@Composable
fun ConteudoPrincipal() {
    var ecraAtual by remember { mutableStateOf("inicio") }
    val historicoEstras = remember { mutableStateListOf<String>() }

    fun navegarPara(ecra: String) {
        if (ecraAtual != ecra) {
            historicoEstras.add(ecraAtual)
            ecraAtual = ecra
        }
    }

    fun voltarAtras() {
        if (historicoEstras.isNotEmpty()) {
            ecraAtual = historicoEstras.removeAt(historicoEstras.size - 1)
        }
    }

    BackHandler(enabled = historicoEstras.isNotEmpty()) {
        voltarAtras()
    }

    when (ecraAtual) {
        "inicio" -> HomeScreen(
            onLinkCheckerClick = { navegarPara("verificador_links") },
            onNetworkClick = { navegarPara("rede") },
            onAppDetectorClick = { navegarPara("detetor_apps") },
            onWifiAnalyzerClick = { navegarPara("wifi") },
            onPasswordCheckerClick = { navegarPara("senhas") },
            onPermissionMonitorClick = { navegarPara("permissoes") },
            onSettingsClick = { navegarPara("definicoes") },
            onAlertCenterClick = { navegarPara("alertas") },
            onSecurityScoreClick = { navegarPara("pontuacao") },
            onAboutClick = { navegarPara("sobre") },
            onPoliciesClick = { navegarPara("politicas") },
            onContactClick = { navegarPara("contacto") }
        )
        "verificador_links" -> LinkCheckerScreen { voltarAtras() }
        "rede" -> NetworkStatsScreen { voltarAtras() }
        "detetor_apps" -> AppDetectorScreen { voltarAtras() }
        "wifi" -> WifiAnalyzerScreen { voltarAtras() }
        "senhas" -> PasswordCheckerScreen { voltarAtras() }
        "permissoes" -> PermissionMonitorScreen { voltarAtras() }
        "definicoes" -> SettingsScreen { voltarAtras() }
        "alertas" -> AlertCenterScreen(
            onBack = { voltarAtras() },
            onNavigateTo = { target -> navegarPara(target) },
        )
        "pontuacao" -> SecurityScoreScreen { voltarAtras() }
        "sobre" -> AboutScreen { voltarAtras() }
        "politicas" -> PoliciesScreen { voltarAtras() }
        "contacto" -> ContactScreen { voltarAtras() }
    }
}