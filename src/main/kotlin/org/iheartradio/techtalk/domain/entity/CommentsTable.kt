package org.iheartradio.techtalk.domain.entity

import org.jetbrains.exposed.dao.LongIdTable

object CommentsTable : LongIdTable() {
    val userId = long("USER_ID")
    val body = varchar(name = "BODY", length = POST_MAX_CHARS)
    val date = datetime(name = "DATE")
    val likeRating = integer("LIKE_RATING")
    val repostCount = integer("REPOST_COUNT")
    val shareCount = integer("SHARE_COUNT")
    val post = reference("POST", PostsTable)
}