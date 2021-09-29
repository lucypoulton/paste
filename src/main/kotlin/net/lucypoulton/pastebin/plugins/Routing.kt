package net.lucypoulton.pastebin.plugins

import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Application.configureRouting() {
    install(Locations) {
    }

    routing {

        get("/") {
            val session: UserSession? = call.sessions.get<UserSession>()
            call.respond(FreeMarkerContent("index.ftl", mapOf("session" to session)))
        }

        route("/api") {

        }
    }
}
