package org.iheartradio.techtalk.service

import com.amdelamar.jhash.Hash
import com.amdelamar.jhash.algorithms.Type
import org.iheartradio.techtalk.controller.deleteAll
import org.iheartradio.techtalk.domain.dao.UserDao
import org.iheartradio.techtalk.domain.dao.toUser
import org.iheartradio.techtalk.domain.dao.toUserWithJwt
import org.iheartradio.techtalk.domain.entity.UsersTable
import org.iheartradio.techtalk.model.User
import org.iheartradio.techtalk.utils.APIException
import org.iheartradio.techtalk.utils.ErrorType.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object UserService {

    fun all(): List<User> {
        return transaction {
            UserDao.all().map { it.toUser() }
        }
    }

    fun delete(user: User) {
        transaction { UserDao.findById(user.id)?.delete() }
    }

    fun deleteAll() {
        transaction { UserDao.all().deleteAll() }
    }

    fun new(user: User): User {
        // check if user name exist in database
        if (transaction { UserDao.find { UsersTable.username eq user.username }.firstOrNull() } != null) {
            throw APIException(USERNAME_EXIST)
        }
        if(user.password.isNullOrEmpty()){
            throw APIException(INVALID_PASSWORD)
        }
        return transaction {
            UserDao.new {
                username = user.username
                password_hash = hasher(user.password).create()
            }.toUser()
        }
    }

    fun update(user: User): User {
        return transaction {
            val localUser = UserDao.findById(user.id) ?: throw APIException(USER_NOT_FOUND)
            localUser.apply {
                // TODO: add some fields
            }.toUser()
        }
    }

    fun signIn(user: User): User {
        val localUser = transaction {
            UserDao.find { UsersTable.username.eq(user.username) }
                .firstOrNull()
        } ?: throw APIException(USER_NOT_FOUND)

        if(user.password.isNullOrEmpty()){
            throw APIException(INVALID_PASSWORD)
        }

        if (!verifyPassword(user.password, localUser.password_hash)) {
            throw APIException(INVALID_PASSWORD)
        }

        transaction { localUser.jwt = "${localUser.username}|${UUID.randomUUID()}" }
        return localUser.toUserWithJwt()
    }

    fun auth(jwt: String): AuthResult {
        val username = jwt.split("|").first()
        val user = transaction {
            UserDao.find { UsersTable.username eq username }
                .firstOrNull()
        }
        return AuthResult(success = jwt == user?.jwt, authorizedUserId = user?.id?.value)
    }

    // helpers

    private val hasher: (String) -> Hash = { password ->
        Hash.password(password.toCharArray())
            .saltLength(20)
            .algorithm(Type.BCRYPT)
    }

    private val verifyPassword: (String, String) -> Boolean = { password, passwordHash ->
        password.isNotEmpty() && hasher(password).verify(passwordHash)
    }

    class AuthResult(val success: Boolean, val authorizedUserId: Long? = null)

}