package com.edissone.privacity.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URI
import java.net.URL
import java.net.UnknownHostException

data class LinkAnalysisResult(
    val url: String,
    val domain: String,
    val ip: String,
    val allIps: String,
    val country: String,
    val asn: String,
    val asnOrg: String,
    val protocol: String,
    val riskScore: Int,
    val riskLevel: String,
    val issues: List<String>,
    val hasHttps: Boolean,
    val isShortener: Boolean,
    val isTyposquatting: Boolean,
    val hasSuspiciousKeywords: Boolean,
    val excessiveSubdomains: Boolean,
    val dnsSpoofDetected: Boolean,
    val isTunnelService: Boolean,
    val tunnelType: String,
    val isCloudflare: Boolean,
    val isGoogleService: Boolean,
    val isCDN: Boolean,
    val domainAgeDays: Int,
    val suspiciousPath: Boolean,
    val hasAtSign: Boolean,
    val hasXn: Boolean,
    val hasExcessiveHyphens: Boolean,
    val isLongDomain: Boolean,
    val httpResponseCode: Int,
    val redirectUrl: String
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
private val yellow = Color(0xFFFFCC00)
private val yellowSoft = Color(0xFFFFF5CC)

//Servicoes de Tunelamento
private val tunnelingServices = listOf(
    "ngrok.io", "ngrok.app", "ngrok-free.app",
    "cloudflared.com", "trycloudflare.com",
    "localtunnel.me", "serveo.net",
    "playit.gg", "playit.us",
    "bore.pub",
    "localhost.run",
    "sish.dev",
    "tunnel.lol",
    "forwarding.net",
    "xip.io",
    "nip.io",
    "beame.io",
    "openode.io",
    "sshporto.com",
    "edgecompute.app"
)

// Ranges de IP Cloudflare para verificacao
private val cloudflareCIDRs = listOf(
    "103.21.244.", "103.22.200.", "103.31.4.",
    "104.16.", "104.17.", "104.18.", "104.19.",
    "104.20.", "104.21.", "104.22.", "104.23.",
    "104.24.", "104.25.", "104.26.", "104.27.",
    "104.28.", "104.29.", "104.30.", "104.31.",
    "108.162.", "131.0.72.", "141.101.",
    "162.158.", "162.159.234.", "162.159.235.",
    "162.159.236.", "162.159.237.",
    "172.64.", "172.65.", "172.66.", "172.67.",
    "172.68.", "172.69.", "172.70.", "172.71.",
    "173.245.48.", "173.245.49.",
    "188.114.", "190.93.240.", "190.93.241.",
    "197.234.240.", "197.234.241.",
    "198.41.128.", "198.41.129.", "198.41.130.",
    "198.41.131.", "198.41.132.", "198.41.133.",
    "198.41.134.", "198.41.135.", "198.41.136.",
    "198.41.137.", "198.41.138.", "198.41.139.",
    "198.41.140.", "198.41.141.", "198.41.142.",
    "198.41.143.", "198.41.144.", "198.41.145.",
    "198.41.146.", "198.41.147.", "198.41.148.",
    "198.41.149.", "198.41.150.", "198.41.151.",
    "198.41.152.", "198.41.153.", "198.41.154.",
    "198.41.155.", "198.41.156.", "198.41.157.",
    "198.41.158.", "198.41.159.", "198.41.160.",
    "198.41.161.", "198.41.162.", "198.41.163.",
    "198.41.164.", "198.41.165.", "198.41.166.",
    "198.41.167.", "198.41.168.", "198.41.169.",
    "198.41.170.", "198.41.171.", "198.41.172.",
    "198.41.173.", "198.41.174.", "198.41.175.",
    "198.41.176.", "198.41.177.", "198.41.178.",
    "198.41.179.", "198.41.180.", "198.41.181.",
    "198.41.182.", "198.41.183.", "198.41.184.",
    "198.41.185.", "198.41.186.", "198.41.187.",
    "198.41.188.", "198.41.189.", "198.41.190.",
    "198.41.191.", "198.41.192.", "198.41.193.",
    "198.41.194.", "198.41.195.", "198.41.196.",
    "198.41.197.", "198.41.198.", "198.41.199.",
    "198.41.200.", "198.41.201.", "198.41.202.",
    "198.41.203.", "198.41.204.", "198.41.205.",
    "198.41.206.", "198.41.207.", "198.41.208.",
    "198.41.209.", "198.41.210.", "198.41.211.",
    "198.41.212.", "198.41.213.", "198.41.214.",
    "198.41.215.", "198.41.216.", "198.41.217.",
    "198.41.218.", "198.41.219."
)

// Ranges ngrok conhecidos
private val ngrokRanges = listOf(
    "3.", "18.", "35.", "44.", "50.", "52.", "54.",
    "107.", "184.",
    // Sub-ranges comuns onde ngrok hospeda
    "3.12.", "3.13.", "3.14.", "3.15.",
    "3.16.", "3.17.", "3.18.", "3.19.",
    "3.20.", "3.21.", "3.22.", "3.23.",
    "3.24.", "3.25.", "3.26.", "3.27.",
    "3.80.", "3.81.", "3.82.", "3.83.",
    "3.84.", "3.85.", "3.86.", "3.87.",
    "3.88.", "3.89.", "3.90.", "3.91.",
    "3.92.", "3.93.", "3.94.", "3.95.",
    "3.101.", "3.102.", "3.103.", "3.104.",
    "3.105.", "3.106.", "3.107.", "3.108.",
    "3.128.", "3.129.", "3.130.", "3.131.",
    "3.132.", "3.133.", "3.134.", "3.135.",
    "3.136.", "3.137.", "3.138.", "3.139.",
    "3.140.", "3.141.", "3.142.", "3.143.",
    "3.144.", "3.145.", "3.146.", "3.147.",
    "3.208.", "3.209.", "3.210.", "3.211.",
    "3.212.", "3.213.", "3.214.", "3.215.",
    "3.216.", "3.217.", "3.218.", "3.219.",
    "3.220.", "3.221.", "3.222.", "3.223.",
    "3.224.", "3.225.", "3.226.", "3.227.",
    "3.228.", "3.229.", "3.230.", "3.231.",
    "3.232.", "3.233.", "3.234.", "3.235.",
    "3.236.", "3.237.", "3.238.", "3.239.",
    "18.116.", "18.117.", "18.118.", "18.119.",
    "18.188.", "18.189.", "18.190.", "18.191.",
    "18.192.", "18.193.", "18.194.", "18.195.",
    "18.196.", "18.197.", "18.198.", "18.199.",
    "18.216.", "18.217.", "18.218.", "18.219.",
    "18.220.", "18.221.", "18.222.", "18.223.",
    "35.71.", "35.72.", "35.73.", "35.74.",
    "35.75.", "35.76.", "35.77.", "35.78.",
    "35.79.", "35.80.", "35.81.", "35.82.",
    "35.83.", "35.84.", "35.85.", "35.86.",
    "44.192.", "44.193.", "44.194.", "44.195.",
    "44.196.", "44.197.", "44.198.", "44.199.",
    "44.200.", "44.201.", "44.202.", "44.203.",
    "44.204.", "44.205.", "44.206.", "44.207.",
    "44.208.", "44.209.", "44.210.", "44.211.",
    "44.212.", "44.213.", "44.214.", "44.215.",
    "44.216.", "44.217.", "44.218.", "44.219.",
    "44.220.", "44.221.", "44.222.", "44.223.",
    "44.224.", "44.225.", "44.226.", "44.227.",
    "44.228.", "44.229.", "44.230.", "44.231.",
    "44.232.", "44.233.", "44.234.", "44.235.",
    "44.236.", "44.237.", "44.238.", "44.239.",
    "52.0.", "52.1.", "52.2.", "52.3.",
    "52.4.", "52.5.", "52.6.", "52.7.",
    "52.8.", "52.9.", "52.10.", "52.11.",
    "52.12.", "52.13.", "52.14.", "52.15.",
    "52.44.", "52.45.", "52.46.", "52.47.",
    "52.48.", "52.49.", "52.50.", "52.51.",
    "52.52.", "52.53.", "52.54.", "52.55.",
    "52.56.", "52.57.", "52.58.", "52.59.",
    "52.60.", "52.61.", "52.62.", "52.63.",
    "52.64.", "52.65.", "52.66.", "52.67.",
    "52.68.", "52.69.", "52.70.", "52.71.",
    "52.72.", "52.73.", "52.74.", "52.75.",
    "52.76.", "52.77.", "52.78.", "52.79.",
    "52.80.", "52.81.", "52.82.", "52.83.",
    "52.84.", "52.85.", "52.86.", "52.87.",
    "52.88.", "52.89.", "52.90.", "52.91.",
    "52.92.", "52.93.", "52.94.", "52.95.",
    "52.144.", "52.145.", "52.146.", "52.147.",
    "52.148.", "52.149.", "52.150.", "52.151.",
    "52.152.", "52.153.", "52.154.", "52.155.",
    "52.156.", "52.157.", "52.158.", "52.159.",
    "52.200.", "52.201.", "52.202.", "52.203.",
    "52.204.", "52.205.", "52.206.", "52.207.",
    "52.208.", "52.209.", "52.210.", "52.211.",
    "52.212.", "52.213.", "52.214.", "52.215.",
    "52.216.", "52.217.", "52.218.", "52.219.",
    "52.220.", "52.221.", "52.222.", "52.223.",
    "54.144.", "54.145.", "54.146.", "54.147.",
    "54.148.", "54.149.", "54.150.", "54.151.",
    "54.152.", "54.153.", "54.154.", "54.155.",
    "54.156.", "54.157.", "54.158.", "54.159.",
    "54.160.", "54.161.", "54.162.", "54.163.",
    "54.164.", "54.165.", "54.166.", "54.167.",
    "54.168.", "54.169.", "54.170.", "54.171.",
    "54.172.", "54.173.", "54.174.", "54.175.",
    "54.176.", "54.177.", "54.178.", "54.179.",
    "54.180.", "54.181.", "54.182.", "54.183.",
    "54.184.", "54.185.", "54.186.", "54.187.",
    "54.188.", "54.189.", "54.190.", "54.191.",
    "54.196.", "54.197.", "54.198.", "54.199.",
    "54.200.", "54.201.", "54.202.", "54.203.",
    "54.204.", "54.205.", "54.206.", "54.207.",
    "54.208.", "54.209.", "54.210.", "54.211.",
    "54.212.", "54.213.", "54.214.", "54.215.",
    "54.216.", "54.217.", "54.218.", "54.219.",
    "54.220.", "54.221.", "54.222.", "54.223.",
    "54.224.", "54.225.", "54.226.", "54.227.",
    "54.228.", "54.229.", "54.230.", "54.231.",
    "54.232.", "54.233.", "54.234.", "54.235.",
    "54.236.", "54.237.", "54.238.", "54.239.",
    "54.240.", "54.241.", "54.242.", "54.243.",
    "54.244.", "54.245.", "54.246.", "54.247.",
    "107.20.", "107.21.", "107.22.", "107.23.",
    "184.72.", "184.73.", "184.74.", "184.75.",
    "184.76.", "184.77.", "184.78.", "184.79.",
    "184.168.", "184.169.", "184.170.", "184.171.",
    "184.172.", "184.173.", "184.174.", "184.175.",
    "184.176.", "184.177.", "184.178.", "184.179.",
    "184.180.", "184.181.", "184.182.", "184.183.",
    "184.184.", "184.185.", "184.186.", "184.187.",
    "184.188.", "184.189.", "184.190.", "184.191.",
    "184.192.", "184.193.", "184.194.", "184.195."
)

// Serviços Google que podem hospedar phishing
private val googleServices = listOf(
    "googleusercontent.com",
    "blogspot.com", "blogspot.pt",
    "firebaseio.com", "firebaseapp.com",
    "cloudfunctions.net",
    "appspot.com",
    "storage.googleapis.com",
    "drive.google.com",
    "docs.google.com",
    "sites.google.com",
    "script.google.com"
)

// Serviços CDN comuns para detecao adicional
private val cdnServices = listOf(
    "cloudfront.net",
    "azureedge.net",
    "azurewebsites.net",
    "trafficmanager.net",
    "akamaiedge.net",
    "edgesuite.net",
    "fastly.net",
    "stackpathcdn.com",
    "bunnycdn.com",
    "keycdn.com",
    "cdnify.io",
    "section.io",
    "belugacdn.com",
    "cachefly.net"
)

// FUNCAO PRINCIPAL DE VERIFICACAO

private suspend fun isCloudflareIP(ip: String): Boolean {
    return cloudflareCIDRs.any { prefix -> ip.startsWith(prefix) }
}

private suspend fun isNgrokIP(ip: String): Boolean {
    // ngrok hospeda em AWS EC2 maioritariamente
    return ngrokRanges.any { prefix -> ip.startsWith(prefix) }
}

private suspend fun getASNInfo(ip: String): Triple<String, String, String> {
    // Retorna country, asn, org
    return withContext(Dispatchers.IO) {
        try {
            val geoUrl = URL("http://ip-api.com/json/$ip?fields=status,country,as,org")
            val conn = geoUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            val response = reader.readText()
            reader.close()
            conn.disconnect()

            if (response.contains("\"status\":\"success\"")) {
                val country = try {
                    response.split("\"country\":\"")[1].split("\"")[0]
                } catch (_: Exception) { "Unknown" }

                val asn = try {
                    response.split("\"as\":\"")[1].split("\"")[0]
                } catch (_: Exception) { "" }

                val org = try {
                    response.split("\"org\":\"")[1].split("\"")[0]
                } catch (_: Exception) { "" }

                Triple(country, asn, org)
            } else {
                Triple("Unknown", "", "")
            }
        } catch (_: Exception) {
            Triple("Lookup failed", "", "")
        }
    }
}

private fun getTunnelName(url: String, domain: String): String {
    val lower = domain.lowercase()
    val fullUrlLower = url.lowercase()

    return when {
        lower.contains("ngrok") -> "ngrok"
        lower.contains("trycloudflare") || lower.contains("cloudflared") || lower.contains("cf-tunnel") -> "Cloudflare Tunnel"
        lower.contains("localtunnel") -> "localhost.me"
        lower.contains("serveo") -> "Serveo"
        lower.contains("playit") -> "Playit.gg"
        lower.contains("bore.pub") -> "bore"
        lower.contains("localhost.run") -> "localhost.run"
        lower.contains("sish") -> "sish.dev"
        lower.contains("xip.io") || lower.contains("nip.io") -> "SSH Tunnel"
        lower.contains("ngrok-free") -> "ngrok (gratuito)"
        fullUrlLower.contains("ngrok") && !lower.contains("ngrok") -> "ngrok (no domínio)"
        else -> "Serviço de túnel desconhecido"
    }
}

//ANALISE PRINCIPAL

private suspend fun analyzeLink(input: String): LinkAnalysisResult {
    return withContext(Dispatchers.IO) {
        val issues = mutableListOf<String>()
        var score = 0

        val cleanUrl = input.trim()
        val uri = try { URI(cleanUrl) } catch (_: Exception) { null }
        val domain = uri?.host?.lowercase()?.removePrefix("www.") ?: "unknown"
        val protocol = uri?.scheme ?: ""
        val domainLower = domain.lowercase()
        val fullUrlLower = cleanUrl.lowercase()

        // === Resolucao do IP ===
        var ip = "Unknown"
        var allIps = ""
        var dnsSpoofDetected = false
        var isCloudflare = false
        var isTunnelService = false
        var tunnelType = ""
        var isGoogleService = false
        var isCDN = false
        var httpCode = 0
        var redirectUrl = ""
        var country = "Unknown"
        var asn = ""
        var asnOrg = ""

        try {
            val addresses = InetAddress.getAllByName(domain)
            ip = addresses.firstOrNull()?.hostAddress ?: "Unresolved"
            allIps = addresses.map { it.hostAddress }.filterNotNull().joinToString(", ")

            //VERIFICACOES DE TUNEL

            // 1- Verificar se o dominio e de um servico de tunel
            val isTunnelDomain = tunnelingServices.any { domainLower.contains(it) || fullUrlLower.contains(it) }
            if (isTunnelDomain) {
                isTunnelService = true
                tunnelType = getTunnelName(cleanUrl, domain)
                issues.add("Serviço de túnel detectado: $tunnelType - usado por phishers para esconder servidores reais")
                score += 35
            }

            // 2- Verificar por IP se esta atras de Cloudflare
            isCloudflare = isCloudflareIP(ip)
            if (isCloudflare && !isTunnelService) {

                issues.add("Site protegido por Cloudflare - IP real escondido")
                score += 5
            }

            // 3- Verificar se e ngrok pelo IP
            if (!isTunnelService && isNgrokIP(ip)) {
                isTunnelService = true
                tunnelType = "ngrok (IP AWS EC2)"
                issues.add("IP hospedado em região AWS usada por ngrok - possível serviço de túnel")
                score += 30
            }

            // 4= Verificar servicos Google
            isGoogleService = googleServices.any { domainLower.contains(it) }
            if (isGoogleService) {
                issues.add("Alojado em serviço Google ($domain) - usado em campanhas de phishing")
                score += 15
            }

            // 5- Verificar outros CDNs
            isCDN = cdnServices.any { domainLower.contains(it) }
            if (isCDN && !isTunnelService) {
                issues.add("Alojado em CDN ($domain) - verificar conteúdo real")
                score += 3
            }

            // 6- DNS Spoofing ou IP privado
            if (ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("172.16.") || ip == "127.0.0.1" || ip == "0.0.0.0") {
                if (!domain.contains("localhost") && !domain.contains("local")) {
                    dnsSpoofDetected = true
                    issues.add("DNS Spoofing: IP resolve para range privado ($ip)")
                    score += 40
                }
            }

            // 7- Informacao ASN / GeoIP
            val (geoCountry, geoAsn, geoOrg) = getASNInfo(ip)
            country = geoCountry
            asn = geoAsn
            asnOrg = geoOrg

            // 8- HTTP Request para ver redirecionamentos
            try {
                val conn = URL(cleanUrl).openConnection() as HttpURLConnection
                conn.instanceFollowRedirects = false
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.connect()
                httpCode = conn.responseCode

                if (httpCode in 300..399) {
                    val location = conn.getHeaderField("Location")
                    if (location != null) {
                        redirectUrl = location

                        val redirectLower = location.lowercase()
                        if (tunnelingServices.any { redirectLower.contains(it) }) {
                            issues.add("Redireciona para serviço de túnel: $location")
                            score += 20
                        } else if (googleServices.any { redirectLower.contains(it) }) {
                            issues.add("Redireciona para serviço Google: $location")
                            score += 10
                        }
                    }
                }
                conn.disconnect()
            } catch (_: Exception) {
                issues.add("Não foi possível contactar o servidor HTTP")
                score += 5
            }

        } catch (_: UnknownHostException) {
            ip = "Unresolved"
            issues.add("Domínio não resolve para nenhum endereço IP")
            score += 25
        } catch (_: Exception) {
            ip = "Error resolving"
            issues.add("Erro ao resolver domínio")
            score += 10
        }


        val hasHttps = protocol == "https" || cleanUrl.startsWith("https://")
        if (!hasHttps) {
            issues.add("Conexão não encriptada (HTTP)")
            score += 20
        }


        val shorteners = listOf(
            "bit.ly", "bitly.com",
            "tinyurl.com", "tiny.cc",
            "t.co", "ow.ly",
            "is.gd", "buff.ly",
            "cutt.ly", "shorturl.at",
            "rebrand.ly", "short.link",
            "tr.im", "v.gd",
            "shorte.st", "clicky.me",
            "tiny.one", "b.link",
            "bl.ink", "shortcm.xyz",
            "migre.me", "encurtador.com.br"
        )
        val isShortener = shorteners.any { domainLower.contains(it) }
        if (isShortener) {
            issues.add("Encurtador de URL - destino oculto")
            score += 20
        }


        val trustedSites = listOf(
            "google.com", "gmail.com", "youtube.com", "github.com",
            "microsoft.com", "outlook.com", "office.com",
            "openai.com", "chatgpt.com",
            "apple.com", "icloud.com",
            "amazon.com", "amazon.pt",
            "netflix.com", "cloudflare.com",
            "linkedin.com", "whatsapp.com",
            "telegram.org", "instagram.com",
            "facebook.com", "messenger.com",
            "twitter.com", "x.com",
            "paypal.com", "stripe.com",
            "binance.com", "coinbase.com",
            "santander.pt", "cgd.pt", "millenniumbcp.pt",
            "novobanco.pt", "activo.pt",
            "ctt.pt", "n26.com", "revolut.com",
            "wise.com", "transferwise.com"
        )

        val isTyposquatting = trustedSites.any { trusted ->
            val dist = levenshtein(domainLower, trusted)
            dist in 1..3 && domainLower != trusted
        }
        if (isTyposquatting) {
            issues.add("Possível typosquatting: domínio semelhante a site conhecido")
            score += 35
        }


        val suspiciousKeywords = listOf(
            "login", "signin", "sign-in", "verify", "secure",
            "account", "update", "confirm", "password",
            "credential", "banking", "wallet", "authenticate",
            "recover", "unlock", "suspend", "alert",
            "blocked", "security", "important", "urgent",
            "notification", "unusual", "suspicious",
            "verification", "validate", "2fa", "mfa",
            "payment", "billing", "invoice", "receipt",
            "tax", "refund", "prize", "winner", "lottery"
        )
        val hasSuspiciousKeywords = suspiciousKeywords.any { domain.contains(it) }
        if (hasSuspiciousKeywords) {
            issues.add("Domínio contém palavras suspeitas (login, verify, secure...)")
            score += 15
        }


        val excessiveSubdomains = domain.count { it == '.' } >= 3
        if (excessiveSubdomains) {
            issues.add("Níveis excessivos de subdomínio (possível phishing)")
            score += 10
        }


        val isLongDomain = domain.length > 30
        if (isLongDomain) {
            issues.add("Nome de domínio anormalmente longo")
            score += 8
        }


        val hasExcessiveHyphens = domain.count { it == '-' } > 3
        if (hasExcessiveHyphens) {
            issues.add("Hífenes excessivos no domínio")
            score += 8
        }


        val hasAtSign = domain.contains("@")
        if (hasAtSign) {
            issues.add("URL contém '@' - padrão de roubo de credenciais")
            score += 30
        }


        val hasXn = domain.contains("xn--")
        if (hasXn) {
            issues.add("Domínio internacionalizado (possível ataque homográfico)")
            score += 25
        }


        val path = uri?.path?.lowercase() ?: ""
        val suspiciousPath = path.contains("login") || path.contains("verify") ||
                path.contains("secure") || path.contains("account") ||
                path.contains("sign") || path.contains("auth") ||
                path.contains("banking") || path.contains("confirm") ||
                path.contains("token") || path.contains("password") ||
                path.contains("2fa") || path.contains("wallet") ||
                path.contains("recover") || path.contains("reset") ||
                path.contains("validation") || path.contains("update")
        if (suspiciousPath) {
            issues.add("Caminho suspeito na URL (página de login/verificação)")
            score += 12
        }


        val isTrusted = trustedSites.any { domain == it || domain.endsWith(".$it") }
        if (isTrusted && !isTyposquatting && !suspiciousPath) {
            score = 0
            issues.clear()
            issues.add("✓ Domínio verificado e confiável")
            country = "Verified"
            asn = "Trusted"
            asnOrg = ""
        }


        val finalScore = score.coerceIn(0, 100)
        val level = when {
            finalScore >= 70 -> "CRÍTICO"
            finalScore >= 45 -> "ALTO"
            finalScore >= 20 -> "MÉDIO"
            finalScore >= 1 -> "BAIXO"
            else -> "SEGURO"
        }

        LinkAnalysisResult(
            url = cleanUrl.take(120),
            domain = domain,
            ip = ip,
            allIps = allIps,
            country = country,
            asn = asn,
            asnOrg = asnOrg,
            protocol = protocol.ifEmpty { "unknown" },
            riskScore = finalScore,
            riskLevel = level,
            issues = issues.distinct(),
            hasHttps = hasHttps,
            isShortener = isShortener,
            isTyposquatting = isTyposquatting,
            hasSuspiciousKeywords = hasSuspiciousKeywords,
            excessiveSubdomains = excessiveSubdomains,
            dnsSpoofDetected = dnsSpoofDetected,
            isTunnelService = isTunnelService,
            tunnelType = tunnelType,
            isCloudflare = isCloudflare,
            isGoogleService = isGoogleService,
            isCDN = isCDN,
            domainAgeDays = -1,
            suspiciousPath = suspiciousPath,
            hasAtSign = hasAtSign,
            hasXn = hasXn,
            hasExcessiveHyphens = hasExcessiveHyphens,
            isLongDomain = isLongDomain,
            httpResponseCode = httpCode,
            redirectUrl = redirectUrl
        )
    }
}



private fun levenshtein(a: String, b: String): Int {
    val dp = Array(a.length + 1) { IntArray(b.length + 1) }
    for (i in a.indices) dp[i][0] = i
    for (j in b.indices) dp[0][j] = j
    for (i in 1..a.length) {
        for (j in 1..b.length) {
            val cost = if (a[i - 1] == b[j - 1]) 0 else 1
            dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
        }
    }
    return dp[a.length][b.length]
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LinkCheckerScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var urlInput by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<LinkAnalysisResult?>(null) }
    var history by remember { mutableStateOf(listOf<LinkAnalysisResult>()) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedResult by remember { mutableStateOf<LinkAnalysisResult?>(null) }

    Scaffold(
        containerColor = bg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // === TOP BAR ===
            Surface(color = white, shadowElevation = 0.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = textDark)
                        }
                        Spacer(Modifier.width(4.dp))
                        Column {
                            Text(
                                "Analisador de Links",
                                color = textDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                "Deteção de phishing e URLs suspeitas",
                                color = textGray,
                                fontSize = 12.sp
                            )
                        }
                    }
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
                        Icon(
                            Icons.Filled.Link,
                            contentDescription = null,
                            tint = blue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Insira o URL para analisar",
                            color = textDark,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        placeholder = { Text("https://exemplo.com", color = textGray.copy(alpha = 0.5f)) },
                        singleLine = true,
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
                            if (urlInput.isNotBlank()) {
                                IconButton(onClick = { urlInput = ""; result = null }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Limpar", tint = textGray)
                                }
                            }
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (urlInput.isNotBlank() && !isAnalyzing) {
                                isAnalyzing = true
                                scope.launch {
                                    val r = analyzeLink(urlInput)
                                    result = r
                                    history = listOf(r) + history
                                    isAnalyzing = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (urlInput.isBlank()) cardBorder else blue
                        ),
                        enabled = urlInput.isNotBlank() && !isAnalyzing
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("A analisar...", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        } else {
                            Icon(Icons.Filled.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Analisar Link", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))


            if (result != null) {
                val r = result!!

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = when {
                                        r.riskScore >= 70 -> red
                                        r.riskScore >= 40 -> orange
                                        r.riskScore >= 10 -> blue
                                        else -> green
                                    },
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Resultado da Análise",
                                    color = textDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when (r.riskLevel) {
                                    "CRÍTICO" -> redSoft
                                    "ALTO" -> red.copy(alpha = 0.1f)
                                    "MÉDIO" -> orangeSoft
                                    "BAIXO" -> blueSoft
                                    else -> greenSoft
                                }
                            ) {
                                Text(
                                    r.riskLevel,
                                    color = when (r.riskLevel) {
                                        "CRÍTICO" -> red
                                        "ALTO" -> red.copy(alpha = 0.8f)
                                        "MÉDIO" -> orange
                                        "BAIXO" -> blue
                                        else -> green
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                r.riskScore >= 70 -> redSoft
                                                r.riskScore >= 40 -> orangeSoft
                                                r.riskScore >= 10 -> blueSoft
                                                else -> greenSoft
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${r.riskScore}",
                                        color = when {
                                            r.riskScore >= 70 -> red
                                            r.riskScore >= 40 -> orange
                                            r.riskScore >= 10 -> blue
                                            else -> green
                                        },
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text("Score Risco", color = textGray, fontSize = 11.sp)
                            }

                            Column {
                                DetailInfo("Domínio", r.domain, textGray, textDark)
                                DetailInfo("IP", r.ip, textGray, textDark)
                                DetailInfo("Protocolo", r.protocol.uppercase(), textGray, textDark)
                                DetailInfo("País", r.country, textGray, textDark)
                                DetailInfo("ASN", r.asn.take(25), textGray, textDark)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Tunnel detection highlight
                        if (r.isTunnelService) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = redSoft),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Dns, contentDescription = null, tint = red, modifier = Modifier.size(22.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text("Serviço de Túnel Detectado", color = red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Tipo: ${r.tunnelType}", color = red.copy(alpha = 0.7f), fontSize = 12.sp)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }


                        if (r.isCloudflare && !r.isTunnelService) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = orangeSoft),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Cloud, contentDescription = null, tint = orange, modifier = Modifier.size(22.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text("Protegido por Cloudflare", color = orange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("IP real do servidor oculto", color = orange.copy(alpha = 0.7f), fontSize = 12.sp)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }


                        if (r.isGoogleService) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = orangeSoft),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Public, contentDescription = null, tint = orange, modifier = Modifier.size(22.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text("Alojado em Serviço Google", color = orange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Usado em campanhas de phishing", color = orange.copy(alpha = 0.7f), fontSize = 12.sp)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }


                        if (r.dnsSpoofDetected) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = redSoft),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Dns, contentDescription = null, tint = red, modifier = Modifier.size(22.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Text("DNS Spoofing Detectado - IP privado", color = red, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }


                        if (r.issues.isNotEmpty()) {
                            Text(
                                "Problemas Encontrados (${r.issues.size})",
                                color = textGray,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            r.issues.forEach { issue ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Filled.Cancel,
                                        contentDescription = null,
                                        tint = if (issue.contains("✓")) green else orange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        issue.removePrefix("✓ "),
                                        color = textDark,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }


                        Spacer(Modifier.height(12.dp))
                        Text("Verificações de Segurança", color = textGray, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(Modifier.height(6.dp))

                        SecurityCheckItem("HTTPS Encriptado", r.hasHttps, green, red, textDark)
                        SecurityCheckItem("DNS Resolve Válido", r.ip != "Unknown" && r.ip != "Unresolved" && r.ip != "Error resolving", green, red, textDark)
                        SecurityCheckItem("Sem DNS Spoofing", !r.dnsSpoofDetected, green, red, textDark)
                        SecurityCheckItem("Sem Encurtador URL", !r.isShortener, green, red, textDark)
                        SecurityCheckItem("Sem Serviço de Túnel", !r.isTunnelService, green, red, textDark)
                        SecurityCheckItem("Sem Keywords Suspeitas", !r.hasSuspiciousKeywords, green, red, textDark)
                        SecurityCheckItem("Sem Typosquatting", !r.isTyposquatting, green, red, textDark)
                        SecurityCheckItem("Sem Redirecionamento Suspeito", r.redirectUrl.isEmpty() || r.httpResponseCode !in 300..399, green, red, textDark)


                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = {
                                selectedResult = r
                                showDetailDialog = true
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = blue),
                            border = BorderStroke(1.dp, blue.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Ver Detalhes Completos", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }


            if (history.isNotEmpty()) {
                Text(
                    "Histórico",
                    color = textDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(8.dp))

                history.forEachIndexed { index, item ->
                    HistoryCard(
                        item = item,
                        index = index,
                        onClick = { result = item },
                        textDark = textDark,
                        textGray = textGray,
                        white = white,
                        cardBorder = cardBorder,
                        green = green,
                        red = red,
                        orange = orange,
                        blue = blue
                    )
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }


    if (showDetailDialog && selectedResult != null) {
        val r = selectedResult!!
        AlertDialog(
            onDismissRequest = { showDetailDialog = false },
            containerColor = white,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Description, contentDescription = null, tint = blue, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Relatório Completo", fontWeight = FontWeight.Bold, color = textDark)
                }
            },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Text("Informação do Domínio", color = blue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    HorizontalDivider(color = cardBorder)
                    Spacer(Modifier.height(6.dp))
                    DetailInfo("URL", r.url, textGray, textDark)
                    DetailInfo("Domínio", r.domain, textGray, textDark)
                    DetailInfo("IP", r.ip, textGray, textDark)
                    DetailInfo("País", r.country, textGray, textDark)
                    DetailInfo("ASN", r.asn.ifEmpty { "N/A" }, textGray, textDark)
                    DetailInfo("Org", r.asnOrg.ifEmpty { "N/A" }, textGray, textDark)
                    DetailInfo("Protocolo", r.protocol.uppercase(), textGray, textDark)
                    DetailInfo("Código HTTP", "${r.httpResponseCode}", textGray, textDark)

                    if (r.redirectUrl.isNotEmpty()) {
                        DetailInfo("Redireciona para", r.redirectUrl, textGray, if (r.redirectUrl.contains(r.domain)) textDark else red)
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Serviços de Hospedagem", color = orange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    HorizontalDivider(color = cardBorder)
                    Spacer(Modifier.height(6.dp))
                    DetailInfo("Serviço de Túnel", if (r.isTunnelService) "Sim - ${r.tunnelType}" else "Não", textGray, if (r.isTunnelService) red else green)
                    DetailInfo("Cloudflare", if (r.isCloudflare) "Sim" else "Não", textGray, if (r.isCloudflare) orange else green)
                    DetailInfo("Serviço Google", if (r.isGoogleService) "Sim" else "Não", textGray, if (r.isGoogleService) orange else green)
                    DetailInfo("CDN", if (r.isCDN) "Sim" else "Não", textGray, textDark)

                    Spacer(Modifier.height(12.dp))
                    Text("Verificações de Segurança", color = green, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    HorizontalDivider(color = cardBorder)
                    Spacer(Modifier.height(6.dp))
                    DetailInfo("HTTPS", if (r.hasHttps) "✓ Sim" else "✗ Não", textGray, if (r.hasHttps) green else red)
                    DetailInfo("Encurtador", if (r.isShortener) "⚠ Sim" else "✓ Não", textGray, if (r.isShortener) orange else green)
                    DetailInfo("Typosquatting", if (r.isTyposquatting) "⚠ Detectado" else "✓ Limpo", textGray, if (r.isTyposquatting) red else green)
                    DetailInfo("Keywords Suspeitas", if (r.hasSuspiciousKeywords) "⚠ Sim" else "✓ Não", textGray, if (r.hasSuspiciousKeywords) red else green)
                    DetailInfo("Subdomínios Excessivos", if (r.excessiveSubdomains) "⚠ Sim (${r.domain.count { it == '.' }})" else "✓ Normal", textGray, if (r.excessiveSubdomains) orange else green)
                    DetailInfo("DNS Spoofing", if (r.dnsSpoofDetected) "⚠ Detectado" else "✓ Não", textGray, if (r.dnsSpoofDetected) red else green)
                    DetailInfo("Caminho Suspeito", if (r.suspiciousPath) "⚠ Sim" else "✓ Não", textGray, if (r.suspiciousPath) red else green)
                    DetailInfo("@ na URL", if (r.hasAtSign) "⚠ Sim" else "✓ Não", textGray, if (r.hasAtSign) red else green)
                    DetailInfo("IDN Homograph", if (r.hasXn) "⚠ Detectado" else "✓ Não", textGray, if (r.hasXn) red else green)


                    if (r.issues.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text("Problemas (${r.issues.size})", color = red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        HorizontalDivider(color = cardBorder)
                        Spacer(Modifier.height(6.dp))
                        r.issues.forEach { issue ->
                            Row(Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Filled.Circle, contentDescription = null, tint = red, modifier = Modifier.size(6.dp).align(Alignment.Top).padding(top = 4.dp))
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



@Composable
private fun DetailInfo(label: String, value: String, textGray: Color, textValue: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = textGray, fontSize = 12.sp)
        Text(
            value.ifEmpty { "N/A" },
            color = textValue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 140.dp)
        )
    }
}

@Composable
private fun SecurityCheckItem(label: String, passed: Boolean, passColor: Color, failColor: Color, textDark: Color) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (passed) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
            contentDescription = null,
            tint = if (passed) passColor else failColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(label, color = textDark, fontSize = 13.sp)
    }
}

@Composable
private fun HistoryCard(
    item: LinkAnalysisResult,
    index: Int,
    onClick: () -> Unit,
    textDark: Color,
    textGray: Color,
    white: Color,
    cardBorder: Color,
    green: Color,
    red: Color,
    orange: Color,
    blue: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        border = BorderStroke(1.dp, cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.3.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            item.riskScore >= 70 -> redSoft
                            item.riskScore >= 40 -> orangeSoft
                            item.riskScore >= 10 -> blueSoft
                            else -> greenSoft
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("${index + 1}", color = textGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.domain, color = textDark, fontWeight = FontWeight.Medium, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("IP: ${item.ip}${if (item.isTunnelService) " • Túnel" else ""}", color = textGray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (item.riskLevel) {
                    "CRÍTICO" -> redSoft
                    "ALTO" -> red.copy(alpha = 0.1f)
                    "MÉDIO" -> orangeSoft
                    "BAIXO" -> blueSoft
                    else -> greenSoft
                }
            ) {
                Text(
                    "${item.riskScore}",
                    color = when (item.riskLevel) {
                        "CRÍTICO" -> red
                        "ALTO" -> red.copy(alpha = 0.8f)
                        "MÉDIO" -> orange
                        "BAIXO" -> blue
                        else -> green
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}