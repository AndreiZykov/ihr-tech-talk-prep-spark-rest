package org.iheartradio.techtalk.domain

import com.mongodb.ServerAddress
import kotlinx.nosql.CreateDrop
import kotlinx.nosql.Session
import kotlinx.nosql.Update
import kotlinx.nosql.mongodb.MongoDB
import kotlinx.nosql.mongodb.MongoDBSession
import org.iheartradio.techtalk.SQLStatement
import org.iheartradio.techtalk.domain.entity.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


val SALT = System.getenv("TT_SALT")

object DB {

    var mongo: MongoDB? = null

    val db: MongoDB?
        get() = mongo

    fun init() {


        val sql = "HELLO WORLD RETURNS float AS \$dist$"
        println(sql)
        val dbUrl = System.getenv("TT_DB_URL")
        val dbUserName = System.getenv("TT_DB_USER_NAME")
        val dbPassword = System.getenv("TT_DB_PASSWORD")
        Database.connect(url = dbUrl, driver = DB_DRIVER, user = dbUserName, password = dbPassword)
        transaction {
            val tables = arrayOf(UsersTable, PostsTable, RepliesTable, PostExtrasTable)
//            SchemaUtils.drop(*tables.reversedArray())
//            SchemaUtils.drop(PostsTable, RepliesTable, PostExtrasTable)
            SchemaUtils.createMissingTablesAndColumns(*tables)
        }

        DbFunctions.createOrReplace()

//        mongo = MongoDB(
//            database = "test_db",
//            schemas = arrayOf(Users, Posts),
//            action = CreateDrop(onCreate = {
//        }))


//        mongo = MongoDB(
//            database = "test_db",
//            schemas = arrayOf(Users, Posts),
//            action = CreateDrop(onCreate = {})
//        )

        mongo = MongoDB(
            database = "test_db",
            schemas = arrayOf(Users, Posts)
        )
    }

    private const val DB_DRIVER = "org.postgresql.Driver"

    fun <R> withSession(statement: MongoDBSession.() -> R): R? =
        if (mongo != null) {
            Session.threadLocale.set(mongo?.session)
            mongo?.withSession(statement).also { Session.threadLocale.set(null) }
        } else {
            null
        }
}



fun <R> withMongoDbSession(statement: MongoDBSession.() -> R): R? = DB.withSession(statement)
