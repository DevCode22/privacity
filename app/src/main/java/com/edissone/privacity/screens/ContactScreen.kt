package com.edissone.privacity.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
private val greenSoft = Color(0xFFD4F5D4)
private val textDark = Color(0xFF1C1C1E)
private val textGray = Color(0xFF6E6E73)
private val cardBorder = Color(0xFFE5E5EA)
private val purpleSoft = Color(0xFFE8D6FF)
private val purple = Color(0xFF8E44AD)
private val tealSoft = Color(0xFFD6F5F5)
private val teal = Color(0xFF1ABC9C)
private val red = Color(0xFFFF3B30)
private val redSoft = Color(0xFFFFD6D6)
private val navy = Color(0xFF2C3E50)
private val navySoft = Color(0xFFD6E0E8)
private val pink = Color(0xFFE1306C)
private val pinkSoft = Color(0xFFFFD6E6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current

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
                    Text("Contactos", color = textDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {


                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(blueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Android,
                                contentDescription = null,
                                tint = blue,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Privacity",
                            color = textDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )

                        Text(
                            "Código Aberto • Segurança • Privacidade",
                            color = textGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            "Siga-nos nas redes sociais ou contribua no GitHub!",
                            color = blue,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                //CONTACTO DIRETO
                Text(
                    "Contacto Direto",
                    color = textDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(10.dp))

                ContactMethodCard(
                    icon = Icons.Outlined.Mail,
                    title = "Gmail",
                    subtitle = "kanhandulaedissone@gmail.com",
                    description = "Resposta em até 24 horas úteis",
                    iconColor = red,
                    bgColor = redSoft,
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:kanhandulaedissone@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Privacity - Contacto")
                        }
                        context.startActivity(intent)
                    }
                )

                Spacer(Modifier.height(16.dp))

                //REDES SOCIAIS
                Text(
                    "Redes Sociais",
                    color = textDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(10.dp))

                // GitHub
                SocialCard(
                    icon = Icons.Outlined.Code,
                    title = "GitHub",
                    subtitle = "github.com/DevCode22/privacity.git",
                    description = "Código-fonte oficial, contribuições e issues",
                    iconColor = navy,
                    bgColor = navySoft,
                    url = "https://github.com/DevCode22/privacity.git"
                )

                Spacer(Modifier.height(10.dp))

                // LinkedIn
                SocialCard(
                    icon = Icons.Outlined.Badge,
                    title = "LinkedIn",
                    subtitle = "linkedin.com/in/devcode22k",
                    description = "Perfil profissional e networking",
                    iconColor = blue,
                    bgColor = blueSoft,
                    url = "https://linkedin.com/in/devcode22k"
                )

                Spacer(Modifier.height(10.dp))

                // Facebook
                SocialCard(
                    icon = Icons.Outlined.Groups,
                    title = "Facebook",
                    subtitle = "facebook.com/profile.php?id=61590473921863",
                    description = "Página oficial do Privacity",
                    iconColor = blue,
                    bgColor = navySoft,
                    url = "https://facebook.com/profile.php?id=61590473921863"
                )

                Spacer(Modifier.height(10.dp))

                // Instagram
                SocialCard(
                    icon = Icons.Outlined.CameraAlt,
                    title = "Instagram",
                    subtitle = "@devcode22k",
                    description = "Dicas de segurança e novidades",
                    iconColor = pink,
                    bgColor = pinkSoft,
                    url = "https://instagram.com/devcode22k"
                )

                Spacer(Modifier.height(10.dp))

                // YouTube
                SocialCard(
                    icon = Icons.Outlined.PlayCircle,
                    title = "YouTube",
                    subtitle = "youtube.com/@devcode22k",
                    description = "Tutoriais, análises e demonstrações",
                    iconColor = red,
                    bgColor = redSoft,
                    url = "https://youtube.com/@devcode22k"
                )

                Spacer(Modifier.height(20.dp))

                // REPOSITORIO OFICIAL
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Code, contentDescription = null, tint = navy, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Repositório Oficial", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Código Aberto - Licença MIT", color = textGray, fontSize = 12.sp)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "O Privacity é um projeto de código aberto. " +
                                    "Contribua, reporte bugs ou sugira melhorias no nosso repositório oficial do GitHub.",
                            color = textDark,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DevCode22/privacity.git"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = navy),
                            border = BorderStroke(1.5.dp, navy.copy(alpha = 0.4f))
                        ) {
                            Icon(Icons.Outlined.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Ver no GitHub", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                //REPORTAR ERROS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.BugReport, contentDescription = null, tint = orange, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Reportar Problemas", color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Ajude-nos a melhorar", color = textGray, fontSize = 12.sp)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // GitHub
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DevCode22/privacity/issues"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = blue),
                                border = BorderStroke(1.dp, blue.copy(alpha = 0.4f))
                            ) {
                                Icon(Icons.Outlined.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Issues", fontSize = 12.sp)
                            }

                            // Email
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:kanhadulaedissone@gmail.com")
                                        putExtra(Intent.EXTRA_SUBJECT, "Privacity - Reporte de Erro")
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = red),
                                border = BorderStroke(1.dp, red.copy(alpha = 0.4f))
                            ) {
                                Icon(Icons.Outlined.Mail, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Email", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))


                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Privacity v1.0.0 • Código Aberto",
                            color = textGray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Licença MIT • 2026 DevCode",
                            color = textGray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ContactMethodCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    description: String,
    iconColor: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = textDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, color = iconColor, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                Text(description, color = textGray, fontSize = 10.sp)
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = textGray.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SocialCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    description: String,
    iconColor: Color,
    bgColor: Color,
    url: String
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = textDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, color = iconColor, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                Text(description, color = textGray, fontSize = 10.sp)
            }

            Icon(
                Icons.Filled.OpenInNew,
                contentDescription = null,
                tint = iconColor.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}