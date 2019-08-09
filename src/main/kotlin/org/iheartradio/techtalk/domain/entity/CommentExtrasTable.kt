package org.iheartradio.techtalk.domain.entity

import org.jetbrains.exposed.dao.LongIdTable

object CommentExtrasTable : LongIdTable() {
    val userId = long("USER_ID").primaryKey()
    val commentId = long("COMMENT_ID").primaryKey()
    val like = integer("LIKE")
    val repost = integer("REPOST")
    val share = integer("SHARE")
}