package net.lucypoulton.pastebin

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.features.AutoHeadResponse.install
import io.ktor.freemarker.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.lucypoulton.pastebin.plugins.configureRouting
import net.lucypoulton.pastebin.plugins.configureSecurity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    // TODO - proper config, external dbs?
    Database.connect("jdbc:h2:./database", "org.h2.Driver")
    transaction {
        SchemaUtils.create(Pastes)
        SchemaUtils.create(Privileges)
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "/templates")
    }

    configureRouting()
    configureSecurity()
}
