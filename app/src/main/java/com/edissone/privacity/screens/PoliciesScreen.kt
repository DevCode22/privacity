package com.edissone.privacity.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliciesScreen(onBack: () -> Unit = {}) {
    var selectedSection by remember { mutableIntStateOf(0) }
    val sections = listOf("Privacidade", "Segurança", "Cookies", "Termos de Uso")

    Scaffold(
        containerColor = bg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
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
                    Text("Políticas", color = textDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sections.forEachIndexed { index, title ->
                        FilterChip(
                            selected = selectedSection == index,
                            onClick = { selectedSection = index },
                            label = { Text(title, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = blue,
                                selectedLabelColor = Color.White,
                                containerColor = white,
                                labelColor = textDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedSection == index,
                                borderColor = cardBorder,
                                selectedBorderColor = blue
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))


                when (selectedSection) {
                    0 -> PrivacyPolicyContent()
                    1 -> SecurityPolicyContent()
                    2 -> CookiesPolicyContent()
                    3 -> TermsOfUseContent()
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PolicySection(iconColor: Color, bgColor: Color, title: String, content: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(iconColor)
            )
            Spacer(Modifier.width(8.dp))
            Text(title, color = textDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            content,
            color = textDark,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
private fun PrivacyPolicyContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.PrivacyTip, contentDescription = null, tint = blue, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("Política de Privacidade", color = textDark, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }

            Spacer(Modifier.height(16.dp))

            Text("Última atualização: Junho de 2026", color = textGray, fontSize = 12.sp)

            Spacer(Modifier.height(16.dp))

            PolicySection(orange, orangeSoft, "Recolha de Dados",
                "O Privacity recolhe exclusivamente dados necessários para o funcionamento " +
                        "das suas funcionalidades de segurança. Nenhum dado pessoal é recolhido, " +
                        "armazenado ou partilhado com terceiros sem o seu consentimento explícito."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(blue, blueSoft, "Dados Recolhidos",
                "• Estado da rede Wi-Fi (SSID, segurança, intensidade do sinal)\n" +
                        "• Lista de aplicações instaladas (apenas para deteção de apps suspeitas)\n" +
                        "• Permissões concedidas a aplicações\n" +
                        "• Estatísticas de uso de rede (bytes enviados/recebidos)\n" +
                        "• URLs analisados (processados localmente, nunca enviados)"
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(green, greenSoft, "Processamento Local",
                "Todas as análises são processadas localmente no seu dispositivo. " +
                        "Nenhum dado é enviado para servidores externos. " +
                        "As verificações de IP utilizam APIs públicas apenas para resolução de DNS e GeoIP."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(orange, orangeSoft, "Partilha de Dados",
                "O Privacity não partilha, vende ou transfere os seus dados a terceiros. " +
                        "Os dados permanecem exclusivamente no seu dispositivo."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(blue, blueSoft, "Segurança dos Dados",
                "Implementamos medidas de segurança técnicas e organizacionais para proteger " +
                        "os seus dados contra acesso não autorizado, alteração ou destruição."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(green, greenSoft, "Código Aberto",
                "Todo o código-fonte do Privacity está disponível publicamente no GitHub para " +
                        "auditoria independente. A transparência é um dos nossos valores fundamentais."
            )
        }
    }
}

@Composable
private fun SecurityPolicyContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Security, contentDescription = null, tint = orange, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("Política de Segurança", color = textDark, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }

            Spacer(Modifier.height(16.dp))

            PolicySection(blue, blueSoft, "Práticas de Segurança",
                "• Todas as análises são realizadas localmente no dispositivo\n" +
                        "• As ligações de rede são monitorizadas em tempo real\n" +
                        "• A deteção de phishing utiliza múltiplas camadas de verificação\n" +
                        "• As senhas nunca são armazenadas - apenas a métrica de força é calculada"
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(blue, blueSoft, "Permissões de Acesso",
                "O aplicativo solicita apenas as permissões estritamente necessárias:\n" +
                        "• Acesso à rede (para análise de Wi-Fi)\n" +
                        "• Localização (para escanear redes próximas)\n" +
                        "• Lista de aplicações (para deteção de apps suspeitas)"
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(blue, blueSoft, "Atualizações de Segurança",
                "Recomendamos manter o Privacity sempre atualizado para beneficiar " +
                        "das últimas correções de segurança e novas funcionalidades de proteção."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(blue, blueSoft, "Reporte de Vulnerabilidades",
                "Se encontrar alguma vulnerabilidade de segurança, por favor contacte-nos " +
                        "através do email oficial ou abra uma issue no GitHub. Agradecemos a sua " +
                        "contribuição para melhorar a segurança de todos os utilizadores."
            )
        }
    }
}

@Composable
private fun CookiesPolicyContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Cookie, contentDescription = null, tint = orange, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("Política de Cookies", color = textDark, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }

            Spacer(Modifier.height(16.dp))

            PolicySection(orange, orangeSoft, "O que são Cookies?",
                "Cookies são pequenos ficheiros de texto armazenados no seu navegador " +
                        "quando visita sites. Eles são usados para lembrar preferências e melhorar a experiência."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(orange, orangeSoft, "Cookies no Privacity",
                "O Privacity NÃO utiliza cookies para tracking ou recolha de dados. " +
                        "A aplicação funciona inteiramente offline e não requer cookies " +
                        "para o seu funcionamento."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(orange, orangeSoft, "Serviços Externos",
                "Quando utiliza o analisador de links para verificar um URL, " +
                        "o Privacity pode contactar servidores DNS públicos (8.8.8.8, 1.1.1.1) " +
                        "apenas para resolver o nome de domínio. Estes servidores não recebem " +
                        "qualquer dado pessoal e seguem as suas próprias políticas de privacidade."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(orange, orangeSoft, "Repositório GitHub",
                "Por ser um projeto de código aberto, pode auditar todo o código-fonte " +
                        "no nosso repositório oficial do GitHub, garantindo total transparência."
            )
        }
    }
}

@Composable
private fun TermsOfUseContent() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Description, contentDescription = null, tint = blue, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("Termos de Uso", color = textDark, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }

            Spacer(Modifier.height(16.dp))

            PolicySection(blue, blueSoft, "Aceitação dos Termos",
                "Ao utilizar o Privacity, concorda com os presentes Termos de Uso. " +
                        "Se não concordar com algum dos termos, não utilize a aplicação."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(blue, blueSoft, "Uso Permitido",
                "O Privacity destina-se exclusivamente a:\n" +
                        "• Fins de segurança e privacidade pessoal\n" +
                        "• Monitorização de dispositivos próprios\n" +
                        "• Educação em cibersegurança\n\n" +
                        "É proibido utilizar a aplicação para atividades ilegais."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(blue, blueSoft, "Limitação de Responsabilidade",
                "O Privacity é fornecido 'como está', sem garantias de qualquer tipo. " +
                        "O utilizador é responsável pela utilização da aplicação e pelas " +
                        "decisões tomadas com base nas análises fornecidas."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(blue, blueSoft, "Código Aberto - Licença MIT",
                "Este software é distribuído sob a Licença MIT. Pode usar, copiar, modificar, " +
                        "fundir, publicar, distribuir e vender cópias do software, desde que o aviso " +
                        "de copyright e a permissão sejam incluídos em todas as cópias."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(blue, blueSoft, "Alterações aos Termos",
                "Reservamo-nos o direito de modificar estes Termos de Uso a qualquer momento. " +
                        "As alterações entram em vigor imediatamente após a publicação na aplicação."
            )

            Spacer(Modifier.height(12.dp))

            PolicySection(blue, blueSoft, "Contacto",
                "Para questões relacionadas com estes Termos de Uso, por favor utilize " +
                        "a secção de Contactos na aplicação ou abra uma issue no GitHub."
            )
        }
    }
}