package net.lucypoulton.pastebin.plugins

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.lucypoulton.pastebin.Config
import net.lucypoulton.pastebin.Privileges
import net.lucypoulton.pastebin.config
import org.jetbrains.exposed.sql.select
import javax.naming.AuthenticationException
import kotlin.collections.set

data class UserSession(val accessToken: String, val username: String, val role: Short)

fun Application.configureSecurity() {

    val client = HttpClient(Apache)

    install(Authentication) {
        oauth("oauth") {
            urlProvider = { "${environment.config.property("server.hostname").getString()}/callback" }
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
        cookie<UserSession>("SESSION") {
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

                val data : JsonElement = client.get(environment.config.property("oauth.userinfoUrl").getString()) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }

                val username = data.jsonObject["username"]?.jsonPrimitive?.content ?:
                throw AuthenticationException("Username was missing")

                val role = Privileges.select { Privileges.user eq username }.maxOf { it[Privileges.role] }

                call.sessions.set(UserSession(principal?.accessToken.toString(), username, role))
                call.respondRedirect("/hello")
            }
        }
    }
}
