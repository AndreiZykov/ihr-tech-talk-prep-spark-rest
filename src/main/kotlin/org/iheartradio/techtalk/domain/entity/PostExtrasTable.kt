package org.iheartradio.techtalk.domain.entity

import org.jetbrains.exposed.dao.LongIdTable

object PostExtrasTable : LongIdTable() {
    val userId = long("USER_ID").primaryKey()
    val postId = long("POST_ID").primaryKey()
    val like = integer("LIKE")
    val repost = integer("REPOST")
}