package net.lucypoulton.pastebin

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Pastes : Table() {
    val pasteId = varchar("id", 8)
    val author = varchar("author", 32)
    val createdAt = timestamp("createdAt")
    val language = text("language")

    override val primaryKey = PrimaryKey(pasteId)
}

object Privileges: Table() {
    val user = varchar("user", 32)
    val role = short("role")
}