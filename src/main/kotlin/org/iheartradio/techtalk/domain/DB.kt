package org.iheartradio.techtalk.domain

import org.iheartradio.techtalk.domain.entity.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


val SALT =  System.getenv("TT_SALT")

object DB {


    fun init() {

        val dbUrl = System.getenv("TT_DB_URL")
        val dbUserName = System.getenv("TT_DB_USER_NAME")
        val dbPassword = System.getenv("TT_DB_PASSWORD")

        println("db: $dbUrl un: $dbUserName pw: $dbPassword")

        Database.connect(url = dbUrl, driver = DB_DRIVER, user = dbUserName, password = dbPassword)
        transaction {
//            SchemaUtils.drop(CommentExtrasTable, CommentsTable, PostsTable, UsersTable)
//            SchemaUtils.createMissingTablesAndColumns(UsersTable, PostsTable, CommentsTable, CommentExtrasTable)

//            SchemaUtils.drop(RepliesTable, PostsTable)
            SchemaUtils.createMissingTablesAndColumns(UsersTable, PostsTable, RepliesTable, PostExtrasTable)
        }
    }

    private const val DB_DRIVER = "org.postgresql.Driver"
}