package org.iheartradio.techtalk.domain

import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.domain.entity.UsersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DB {

    fun init() {
        Database.connect(url = DB_URL, driver = DB_DRIVER, user = USER_NAME, password = PASSWORD)
        transaction {
//            SchemaUtils.drop(UsersTable, PostsTable)
            SchemaUtils.createMissingTablesAndColumns(UsersTable, PostsTable)
        }
    }

    private const val DB_URL = "jdbc:postgresql://localhost:5432/test_db"
    private const val DB_DRIVER = "org.postgresql.Driver"
    private const val USER_NAME = "Anthony"
    private const val PASSWORD = "password"
}