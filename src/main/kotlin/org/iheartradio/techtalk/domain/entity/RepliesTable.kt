package org.iheartradio.techtalk.domain.entity

import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Table

object RepliesTable : LongIdTable() {
    //The main post main by the OP
    val postId = long("POST_ID")
    //The postID of the reply to thr OP
    val replyPostId = long("REPLY_POST_ID")
}