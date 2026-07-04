package com.edissone.privacity.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.log2
import kotlin.math.min

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
private val yellow = Color(0xFFFFCC00)
private val yellowSoft = Color(0xFFFFF5CC)
private val textDark = Color(0xFF1C1C1E)
private val textGray = Color(0xFF6E6E73)
private val cardBorder = Color(0xFFE5E5EA)

data class PasswordResult(
    val password: String,
    val length: Int,
    val score: Int,
    val level: String,
    val levelColor: Color,
    val hasUppercase: Boolean,
    val hasLowercase: Boolean,
    val hasDigits: Boolean,
    val hasSymbols: Boolean,
    val entropy: Double,
    val combinations: Double,
    val combinationsFormatted: String,
    val crackTime: String,
    val crackTimeSeconds: Double,
    val isCommon: Boolean,
    val hasSequences: Boolean,
    val hasRepeats: Boolean,
    val issues: List<String>,
    val tips: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordCheckerScreen(onBack: () -> Unit = {}) {
    var password by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<PasswordResult?>(null) }
    var showPassword by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }

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
                    Column {
                        Text("Verificador de Senhas", color = textDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Análise de força e segurança", color = textGray, fontSize = 12.sp)
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(Modifier.height(8.dp))


                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = blue, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Digite a senha para analisar", color = textDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }

                        Spacer(Modifier.height(14.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                if (it.isNotEmpty()) {
                                    result = analyzePassword(it)
                                } else {
                                    result = null
                                }
                            },
                            placeholder = { Text("A sua senha", color = textGray.copy(alpha = 0.5f)) },
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textDark,
                                unfocusedTextColor = textDark,
                                focusedBorderColor = blue,
                                unfocusedBorderColor = cardBorder,
                                cursorColor = blue
                            ),
                            shape = RoundedCornerShape(14.dp),
                            trailingIcon = {
                                Row {
                                    IconButton(onClick = { showPassword = !showPassword }) {
                                        Icon(
                                            if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = null,
                                            tint = textGray
                                        )
                                    }
                                    if (password.isNotEmpty()) {
                                        IconButton(onClick = { password = ""; result = null }) {
                                            Icon(Icons.Filled.Close, contentDescription = "Limpar", tint = textGray)
                                        }
                                    }
                                }
                            }
                        )

                        if (password.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "${password.length} caracteres",
                                color = textGray,
                                fontSize = 12.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }


                if (result != null) {
                    val r = result!!

                    Spacer(Modifier.height(16.dp))


                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = white),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(CircleShape)
                                    .background(r.levelColor.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${r.score}",
                                        color = r.levelColor,
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "/ 100",
                                        color = textGray,
                                        fontSize = 14.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(130.dp)
                                        .clip(CircleShape)
                                        .border(5.dp, r.levelColor, CircleShape)
                                )
                            }

                            Spacer(Modifier.height(14.dp))

                            Text(
                                r.level,
                                color = r.levelColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                "Tempo para quebrar: ${r.crackTime}",
                                color = textGray,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )

                            if (r.isCommon) {
                                Spacer(Modifier.height(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = redSoft),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Warning, contentDescription = null, tint = red, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Senha muito comum! Evite usar esta senha.", color = red, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))


                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = white),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Estatísticas", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatBox("Comprimento", "${r.length}", textGray, textDark)
                                StatBox("Entropia", "${"%.1f".format(r.entropy)} bits", textGray, textDark)
                                StatBox("Combinações", r.combinationsFormatted, textGray, textDark)
                            }

                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = cardBorder)
                            Spacer(Modifier.height(14.dp))


                            Text("Composição", color = textDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(Modifier.height(8.dp))

                            CharCheck("Maiúsculas (A-Z)", r.hasUppercase, green, red, textDark)
                            CharCheck("Minúsculas (a-z)", r.hasLowercase, green, red, textDark)
                            CharCheck("Números (0-9)", r.hasDigits, green, red, textDark)
                            CharCheck("Símbolos (!@#\$%...)", r.hasSymbols, green, red, textDark)
                            CharCheck("Sem repetições", !r.hasRepeats, green, red, textDark)
                            CharCheck("Sem sequências", !r.hasSequences, green, red, textDark)


                            if (r.issues.isNotEmpty()) {
                                Spacer(Modifier.height(14.dp))
                                HorizontalDivider(color = cardBorder)
                                Spacer(Modifier.height(14.dp))
                                Text("Problemas", color = red, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Spacer(Modifier.height(6.dp))
                                r.issues.forEach { issue ->
                                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Filled.Cancel, contentDescription = null, tint = red, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(issue, color = textDark, fontSize = 12.sp)
                                    }
                                }
                            }


                            if (r.tips.isNotEmpty()) {
                                Spacer(Modifier.height(14.dp))
                                HorizontalDivider(color = cardBorder)
                                Spacer(Modifier.height(14.dp))
                                Text("Dicas para Melhorar", color = orange, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Spacer(Modifier.height(6.dp))
                                r.tips.forEach { tip ->
                                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = orange, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(tip, color = textDark, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {

                    Spacer(Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = white),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Info, contentDescription = null, tint = blue, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Dicas de Segurança", color = textDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Spacer(Modifier.height(12.dp))
                            val tips = listOf(
                                "Use pelo menos 12 caracteres",
                                "Combine letras, números e símbolos",
                                "Evite palavras comuns como \"password\" ou \"123456\"",
                                "Não use informações pessoais (nome, data de nascimento)",
                                "Evite padrões de teclado (qwerty, asdf)",
                                "Use um gestor de senhas para senhas únicas"
                            )
                            tips.forEach { tip ->
                                Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = green, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(tip, color = textDark, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, textGray: Color, textDark: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = textDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = textGray, fontSize = 11.sp)
    }
}

@Composable
private fun CharCheck(label: String, ok: Boolean, green: Color, red: Color, textDark: Color) {
    Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            if (ok) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
            contentDescription = null,
            tint = if (ok) green else red,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(label, color = textDark, fontSize = 13.sp)
    }
}



private val commonPasswords = setOf(
    "123456", "password", "12345678", "qwerty", "123456789", "12345", "1234",
    "111111", "1234567", "sunshine", "qwerty123", "iloveyou", "princess",
    "admin", "welcome", "666666", "abc123", "football", "123123", "monkey",
    "654321", "!@#\$%^&*", "password123", "senha", "1234567890", "master",
    "12345678910", "12345678901", "qwertyuiop", "qwertz", "asdfgh",
    "zxcvbnm", "passw0rd", "letmein", "trustno1", "dragon", "baseball",
    "hunter", "ranger", "thomas", "robert", "andrew", "joshua",
    "121212", "999999", "000000", "777777", "555555", "444444"
)

private val sequences = listOf(
    "123", "234", "345", "456", "567", "678", "789", "890",
    "abc", "bcd", "cde", "def", "efg", "fgh", "ghi", "hij",
    "ijk", "jkl", "klm", "lmn", "mno", "nop", "opq", "pqr",
    "qrs", "rst", "stu", "tuv", "uvw", "vwx", "wxy", "xyz",
    "qwerty", "asdfgh", "zxcvbn", "qwert", "asdfg", "zxcvb",
    "qazwsx", "1qaz2wsx", "3edc4rfv", "!@#\$%", "qwe", "asd", "zxc"
)

private fun analyzePassword(pwd: String): PasswordResult {
    val issues = mutableListOf<String>()
    val tips = mutableListOf<String>()
    var score = 0

    val length = pwd.length
    val hasUppercase = pwd.any { it.isUpperCase() }
    val hasLowercase = pwd.any { it.isLowerCase() }
    val hasDigits = pwd.any { it.isDigit() }
    val hasSymbols = pwd.any { !it.isLetterOrDigit() }

    val isCommon = pwd.lowercase() in commonPasswords


    val hasRepeats = Regex("(.)\\1{2,}").containsMatchIn(pwd)


    val lowerPwd = pwd.lowercase()
    val hasSequences = sequences.any { lowerPwd.contains(it) }


    when {
        length >= 16 -> score += 30
        length >= 12 -> score += 25
        length >= 10 -> score += 18
        length >= 8 -> score += 10
        length >= 6 -> score += 5
        else -> issues.add("Senha muito curta (mínimo 8 caracteres)")
    }


    if (hasUppercase) {
        score += 12
        if (pwd.count { it.isUpperCase() } >= 2) score += 3
    } else {
        issues.add("Sem letras maiúsculas")
        tips.add("Adicione letras maiúsculas (A-Z)")
    }

    if (hasLowercase) {
        score += 8
    } else {
        issues.add("Sem letras minúsculas")
        tips.add("Adicione letras minúsculas (a-z)")
    }

    if (hasDigits) {
        score += 10
        if (pwd.count { it.isDigit() } >= 3) score += 3
    } else {
        issues.add("Sem números")
        tips.add("Adicione números (0-9)")
    }

    if (hasSymbols) {
        score += 18
        if (pwd.count { !it.isLetterOrDigit() } >= 2) score += 5
    } else {
        issues.add("Sem símbolos especiais")
        tips.add("Adicione símbolos (!@#\$% etc.)")
    }


    if (isCommon) {
        score = 0
        issues.clear()
        issues.add("Senha extremamente comum")
        tips.add("Escolha uma senha única, não encontrada em listas")
    }


    if (hasRepeats) {
        score = (score - 15).coerceAtLeast(0)
        issues.add("Caracteres repetidos em sequência")
        tips.add("Evite repetir o mesmo caractere 3+ vezes (ex: aaa)")
    }


    if (hasSequences) {
        score = (score - 15).coerceAtLeast(0)
        issues.add("Contém sequência previsível (ex: 123, abc)")
        tips.add("Evite padrões sequenciais do teclado ou alfabeto")
    }


    var poolSize = 0
    if (hasLowercase) poolSize += 26
    if (hasUppercase) poolSize += 26
    if (hasDigits) poolSize += 10
    if (hasSymbols) poolSize += 33
    if (poolSize == 0) poolSize = 26

    val entropy = length * log2(poolSize.toDouble())

    when {
        entropy >= 80 -> score += 10
        entropy >= 60 -> score += 5
        entropy < 35 && length >= 4 -> {
            issues.add("Entropia baixa (${"%.1f".format(entropy)} bits)")
            tips.add("Aumente o comprimento e variedade de caracteres")
        }
    }

    val finalScore = score.coerceIn(0, 100)


    val combinations = poolSize.toDouble().pow(length)
    val guessesPerSecond = 1_000_000_000_000.0 // 1 trillion/sec (modern GPU)
    val crackSeconds = if (combinations > 0) combinations / guessesPerSecond else 0.0

    val (level, levelColor) = when {
        finalScore >= 85 -> "SEGURA" to green
        finalScore >= 70 -> "FORTE" to Color(0xFF27AE60)
        finalScore >= 50 -> "BOA" to blue
        finalScore >= 30 -> "RAZOÁVEL" to orange
        finalScore >= 15 -> "FRACA" to Color(0xFFE67E22)
        else -> "MUITO FRACA" to red
    }

    return PasswordResult(
        password = pwd,
        length = length,
        score = finalScore,
        level = level,
        levelColor = levelColor,
        hasUppercase = hasUppercase,
        hasLowercase = hasLowercase,
        hasDigits = hasDigits,
        hasSymbols = hasSymbols,
        entropy = entropy,
        combinations = combinations,
        combinationsFormatted = formatCombinations(combinations),
        crackTime = formatCrackTime(crackSeconds),
        crackTimeSeconds = crackSeconds,
        isCommon = isCommon,
        hasSequences = hasSequences,
        hasRepeats = hasRepeats,
        issues = issues.distinct(),
        tips = tips.distinct()
    )
}

private fun formatCombinations(num: Double): String {
    return when {
        num < 1_000 -> "%.0f".format(num)
        num < 1_000_000 -> "${"%.1f".format(num / 1_000)} mil"
        num < 1_000_000_000 -> "${"%.1f".format(num / 1_000_000)} milhões"
        num < 1_000_000_000_000 -> "${"%.2f".format(num / 1_000_000_000)} biliões"
        num < 1_000_000_000_000_000 -> "${"%.2f".format(num / 1_000_000_000_000)} triliões"
        num < 1_000_000_000_000_000_000 -> "${"%.2f".format(num / 1_000_000_000_000_000)} quatriliões"
        else -> "${"%.2f".format(num / 1_000_000_000_000_000_000)} quintilhões"
    }
}

private fun formatCrackTime(seconds: Double): String {
    return when {
        seconds <= 0 -> "instantâneo"
        seconds < 1 -> "menos de 1 segundo"
        seconds < 60 -> "${seconds.roundToInt()} segundos"
        seconds < 3600 -> "${"%.1f".format(seconds / 60)} minutos"
        seconds < 86400 -> "${"%.1f".format(seconds / 3600)} horas"
        seconds < 2592000 -> "${"%.1f".format(seconds / 86400)} dias"
        seconds < 31536000 -> "${"%.1f".format(seconds / 2592000)} meses"
        seconds < 3.1536e10 -> "${"%.1f".format(seconds / 31536000)} anos"
        seconds < 3.1536e13 -> "${"%.0f".format(seconds / 3.1536e10)} séculos"
        seconds < 3.1536e16 -> "${"%.0f".format(seconds / 3.1536e13)} milénios"
        else -> "superior à idade do Universo"
    }
}
