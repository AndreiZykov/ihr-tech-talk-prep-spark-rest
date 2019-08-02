package org.iheartradio.techtalk.domain.entity

import org.jetbrains.exposed.dao.LongIdTable

object UsersTable : LongIdTable() {
    val username = varchar(name = "USER_NAME", length = 100).uniqueIndex()
    val password_hash = varchar(name = "PASSWORD_HASH", length = 500)
    val jwt = varchar(name = "JWT", length = 500).nullable().index()
    val posts = reference("posts", PostsTable).nullable()
}