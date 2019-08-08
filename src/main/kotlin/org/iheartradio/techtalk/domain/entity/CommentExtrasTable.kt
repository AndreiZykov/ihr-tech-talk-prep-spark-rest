package org.iheartradio.techtalk.domain.entity

import org.jetbrains.exposed.dao.LongIdTable

object CommentExtrasTable : LongIdTable() {
    val userId = long("USER_ID")
    val body = varchar(name = "BODY", length = POST_MAX_CHARS)
    val date = datetime(name = "DATE")
    val likesCount = integer("LIKES_COUNT")
}