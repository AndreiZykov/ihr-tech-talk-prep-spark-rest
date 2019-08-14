package org.iheartradio.techtalk.domain.entity

import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Table

object RepliesTable : LongIdTable() {
//    val postId = reference("POST_ID", PostsTable.id)
    val postId = long("POST_ID")
    val replyPostId = long("REPLY_POST_ID")
}