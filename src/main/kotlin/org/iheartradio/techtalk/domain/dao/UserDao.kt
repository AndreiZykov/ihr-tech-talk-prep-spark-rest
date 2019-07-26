package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.domain.entity.UsersTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class UserDao(id: EntityID<Int>) : IntEntity(id) {
    var username by UsersTable.username
    var password_hash by UsersTable.password_hash
    var jwt by UsersTable.jwt

    companion object : IntEntityClass<UserDao>(UsersTable)

}