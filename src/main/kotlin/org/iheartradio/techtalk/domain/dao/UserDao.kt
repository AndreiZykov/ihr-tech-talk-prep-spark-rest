package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.domain.entity.UsersTable
import org.iheartradio.techtalk.shared.User
import org.jetbrains.exposed.dao.*

class UserDao(id: EntityID<Long>) : LongEntity(id) {
    var username by UsersTable.username
    var password_hash by UsersTable.password_hash
    var jwt by UsersTable.jwt
    companion object : LongEntityClass<UserDao>(UsersTable)
}

fun UserDao.toUser() = User(id = id.value, username = username, password = password_hash, jwt = jwt ?: "")