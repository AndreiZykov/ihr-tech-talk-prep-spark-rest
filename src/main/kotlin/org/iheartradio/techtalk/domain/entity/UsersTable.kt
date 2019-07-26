package org.iheartradio.techtalk.domain.entity

import org.jetbrains.exposed.dao.IntIdTable

object UsersTable : IntIdTable() {
    val username = varchar(name = "USER_NAME", length = 50).uniqueIndex()
    val password_hash = varchar(name = "PASSWORD_HASH", length = 500)
    val jwt = varchar(name = "JWT", length = 500).nullable()
}