package net.lucypoulton.pastebin.plugins

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.lucypoulton.pastebin.Privileges
import net.lucypoulton.pastebin.config
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import javax.naming.AuthenticationException
import kotlin.collections.set
import kotlin.properties.Delegates

@Serializable
data class UserSession(val username: String, val role: Int) : Principal

fun Application.configureSecurity() {

    val client = HttpClient(Apache) {
        install(JsonFeature)
    }

    install(Authentication) {
        oauth("oauth") {
            urlProvider = { "${config.server.hostname}/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "oauth",
                    authorizeUrl = config.oauth.authorizeUrl,
                    accessTokenUrl = config.oauth.accessTokenUrl,
                    requestMethod = HttpMethod.Post,
                    clientId = config.oauth.clientId,
                    clientSecret = config.oauth.clientSecret,
                    defaultScopes = config.oauth.scopes
                )
            }
            this.client = client
        }
    }
    install(Sessions) {
        cookie<UserSession>("SESSION", storage = SessionStorageMemory()) {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    routing {
        authenticate("oauth") {
            get("login") {
                call.respondRedirect("/callback")
            }

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()

                val accessToken = principal?.accessToken.toString()

                val data : JsonElement = client.get(config.oauth.userinfoUrl) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }

                val username = data.jsonObject["username"]?.jsonPrimitive?.content ?:
                throw AuthenticationException("Username was missing")

                var role by Delegates.notNull<Short>()

                transaction {
                    role = Privileges.select { Privileges.user eq username }.map { it[Privileges.role] }.maxOrNull() ?: 0
                }

                call.sessions.set(UserSession(username, role.toInt()))
                call.respondRedirect("/")
            }
        }
    }
}
