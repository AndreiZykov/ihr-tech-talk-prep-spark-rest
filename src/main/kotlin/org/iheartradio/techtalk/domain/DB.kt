package org.iheartradio.techtalk.domain

import org.iheartradio.techtalk.domain.entity.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


val SALT = System.getenv("TT_SALT")

object DB {
    fun init() {
        val dbUrl = System.getenv("TT_DB_URL")
        val dbUserName = System.getenv("TT_DB_USER_NAME")
        val dbPassword = System.getenv("TT_DB_PASSWORD")
        Database.connect(url = dbUrl, driver = DB_DRIVER, user = dbUserName, password = dbPassword)
        transaction {
            val tables = arrayOf(UsersTable, PostsTable, RepliesTable, PostExtrasTable)
            SchemaUtils.drop(*tables.reversedArray())
            SchemaUtils.createMissingTablesAndColumns(*tables)
        }
    }

    private const val DB_DRIVER = "org.postgresql.Driver"
}