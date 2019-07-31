package org.iheartradio.techtalk.controller

import com.amdelamar.jhash.Hash
import com.amdelamar.jhash.algorithms.Type.BCRYPT
import com.google.gson.JsonObject
import org.eclipse.jetty.http.HttpStatus
import org.iheartradio.techtalk.domain.dao.PostDao
import org.iheartradio.techtalk.domain.dao.UserDao
import org.iheartradio.techtalk.domain.dao.toPost
import org.iheartradio.techtalk.domain.dao.toUser
import org.iheartradio.techtalk.domain.entity.UsersTable
import org.iheartradio.techtalk.shared.User
import org.iheartradio.techtalk.shared.toJson
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Route
import java.util.*


object UserController {

    val selectAll = Route { _, response ->

        val users = transaction {
            UserDao.all().map { it.toUser() }
        }

        response.status(HttpStatus.OK_200)

        return@Route users.toJson()
    }

    val insertInto = Route { request, response ->
        val user = User from request.body()

        val newUser = transaction {
            UserDao.new {
                username = user.username
                password_hash = hasher(user.password).create()
            }
        }.toUser()

        response.status(HttpStatus.OK_200)

        return@Route newUser.toJson()
    }

    val update = Route { request, response ->
        val user: User = User from request.body()

        val updatesUser = transaction {
            UserDao.findById(user.id)?.apply {
                // TODO: add some fields
            }
        }?.toUser()

        return@Route if (updatesUser != null) {

            response.status(HttpStatus.OK_200)
            updatesUser.toJson()
        } else {
            response.status(400)
            """
                {"error":"not found"}
            """.trimIndent()
        }
    }

    val delete = Route { request, response ->
        val user = User from request.body()
        transaction { UserDao.findById(user.id)?.delete() }
        response.status(HttpStatus.OK_200)
        return@Route """
                {"response":"success"}
            """.trimIndent()
    }


    val deleteAll = Route { request, response ->
        transaction {
            UserDao.all().deleteAll()
        }
        response.status(HttpStatus.OK_200)

        return@Route JsonObject().apply {
            addProperty("response", true)
        }
    }

    val signIn = Route { request, response ->
        val user = User from request.body()
        val localUser = transaction { UserDao.find { UsersTable.username.eq(user.username) }.firstOrNull() }
        if (localUser != null) {
            if (verifyPassword(user.password, localUser.password_hash)) {
                transaction {
                    localUser.jwt = UUID.randomUUID().toString()
                }
                response.status(HttpStatus.OK_200)
                localUser.toUser().toJson()
            } else {
                response.status(400)
                return@Route """
                {"error":"wrong password"}
            """.trimIndent()
            }

        } else {
            response.status(400)
            return@Route """
                {"error":"user not found"}
            """.trimIndent()
        }
    }


    val selectPostsByUser = Route { request, response ->
        val userId: Long? = request.params(":id").toLong()
        val posts = transaction {
            val allPosts = PostDao.all()
            if (userId == null) {
                allPosts.map { it.toPost() }
            } else {
                allPosts.filter { it.user.id.value == userId }.map { it.toPost() }
            }
        }

        response.status(HttpStatus.OK_200)
        return@Route posts.toJson()
    }

    private val hasher: (String) -> Hash = { password ->
        Hash.password(password.toCharArray())
            .saltLength(20)
            .algorithm(BCRYPT)
    }

    private val verifyPassword: (String, String) -> Boolean = { password, passwordHash ->
        password.isNotEmpty() && hasher(password).verify(passwordHash)
    }

}

fun SizedIterable<Entity<*>>.deleteAll() {
    forEach { it.delete() }
}


inline class Password(val value: String)
fun Password.toCharArray() = value.toCharArray()
fun Password.isNotEmpty() = value.isNotEmpty()
fun Hash.verify(password: HashedPassword) = verify(password.value)
inline class HashedPassword(val value: String)