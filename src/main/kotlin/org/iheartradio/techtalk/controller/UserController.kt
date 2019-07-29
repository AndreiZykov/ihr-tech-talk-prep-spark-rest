package org.iheartradio.techtalk.controller

import com.amdelamar.jhash.Hash
import com.amdelamar.jhash.algorithms.Type.BCRYPT
import org.eclipse.jetty.http.HttpStatus
import org.iheartradio.techtalk.domain.dao.UserDao
import org.iheartradio.techtalk.domain.dao.toUser
import org.iheartradio.techtalk.domain.entity.UsersTable

import org.iheartradio.techtalk.shared.User
import org.iheartradio.techtalk.shared.toJson
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Route
import java.util.*


object UserController {

    val fetchAllUsers = Route { _, response ->

        val users = transaction {
            UserDao.all().map { it.toUser() }
        }

        response.status(HttpStatus.OK_200)

        return@Route users.toJson()
    }

    val newUser = Route { request, response ->
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

    val updateUser = Route { request, response ->
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

    val deleteUser = Route { request, response ->
        val user = User from request.body()
        transaction { UserDao.findById(user.id)?.delete() }

        response.status(HttpStatus.OK_200)

        return@Route """
                {"response":"success"}
            """.trimIndent()
    }

    val signIn = Route { request, response ->
        val user = User from request.body()
        val localUser = transaction { UserDao.find { UsersTable.username.eq(user.username) }.firstOrNull() }
        if (localUser != null) {
            if (user.password.isNotEmpty() && hasher(user.password).verify(localUser.password_hash)) {
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

    private fun hasher(password: String) = Hash.password(password.toCharArray())
        .saltLength(20).algorithm(BCRYPT)

}