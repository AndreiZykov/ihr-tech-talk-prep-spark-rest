package org.iheartradio.techtalk.service

import com.amdelamar.jhash.Hash
import com.amdelamar.jhash.algorithms.Type
import org.iheartradio.techtalk.utils.Encryptor
import org.iheartradio.techtalk.controller.deleteAll
import org.iheartradio.techtalk.domain.SALT
import org.iheartradio.techtalk.domain.dao.UserDao
import org.iheartradio.techtalk.domain.dao.toUser
import org.iheartradio.techtalk.domain.dao.toUserWithJwt
import org.iheartradio.techtalk.domain.entity.UsersTable
import org.iheartradio.techtalk.model.User
import org.iheartradio.techtalk.utils.APIException
import org.iheartradio.techtalk.utils.ErrorType.*
import org.iheartradio.techtalk.utils.apiException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object UserService {

    fun all(): List<User> = transaction {
        UserDao.all().map { it.toUser() }
    }

    fun delete(user: User) {
        transaction { UserDao.findById(user.id)?.delete() }
    }

    fun deleteAll() {
        transaction { UserDao.all().deleteAll() }
    }

    fun new(user: User): User  = transaction {

        if (user.password.isNullOrEmpty()) {
            apiException(INVALID_PASSWORD)
        }

        // check if user name exist in database
        val userAlreadyExists = !UserDao.find { UsersTable.username eq user.username }.empty()

        if(userAlreadyExists) {
            apiException(USERNAME_EXIST)
        }

        UserDao.new {
            username = user.username
            password_hash = hasher(user.password).create()
        }.toUser()
    }

    fun update(user: User): User = transaction {
        val localUser = UserDao.findById(user.id) ?: apiException(USER_NOT_FOUND)
        localUser.apply {
            // TODO: add some fields
        }.toUser()
    }

    fun signIn(user: User): User {
        val predicate = UsersTable.username eq user.username
        val localUser = transaction { UserDao.find(predicate).firstOrNull() }
            ?: apiException(USER_NOT_FOUND)

        if (user.password.isNullOrEmpty()) {
            apiException(INVALID_PASSWORD)
        }

        if (!verifyPassword(user.password, localUser.password_hash)) {
            apiException(INVALID_PASSWORD)
        }

        val token = Encryptor(SALT).encrypt("${localUser.username}${DateTime.now().millis}")
        transaction { localUser.jwt = token }
        return localUser.toUserWithJwt()
    }

    fun auth(jwt: String): AuthResult = transaction {
        jwt.split(" ")
            .takeIf { parts -> parts.size == 2 }
            ?.let { parts -> parts.last() }
            ?.let { token ->
                val user = UserDao.find { UsersTable.jwt eq token }.firstOrNull()
                AuthResult(success = token == user?.jwt, authorizedUserId = user?.id?.value)
            }  ?: apiException(INVALID_TOKEN)
    }



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