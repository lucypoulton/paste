package net.lucypoulton.pastebin

import io.ktor.application.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

val Application.config: Config by lazy {
        val configFile = File("config.json")

        if (configFile.createNewFile()) {
            // if this fails then the jarfile is broken
            Application::class.java.getResourceAsStream("/config.json")!!.copyTo(configFile.outputStream())
        }

        return@lazy Json.decodeFromStream(configFile.inputStream())
    }

@Serializable
data class Config(val oauth: OAuthConfig, val server: ServerConfig)

@Serializable
data class OAuthConfig(
    val authorizeUrl: String,
    val accessTokenUrl: String,
    val userinfoUrl: String,
    val clientId: String,
    val clientSecret: String,
    val scopes: List<String>
)

@Serializable
data class ServerConfig(
    val hostname: String,
    val port: Int
)