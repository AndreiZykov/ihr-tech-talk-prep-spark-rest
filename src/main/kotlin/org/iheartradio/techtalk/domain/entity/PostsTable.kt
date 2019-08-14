package org.iheartradio.techtalk.domain.entity

import org.iheartradio.techtalk.domain.entity.UsersTable.nullable
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.or
import org.joda.time.DateTime

const val POST_MAX_CHARS = 500

object PostsTable : LongIdTable() {
    val user = reference("USER", UsersTable)
    val body = varchar(name = "BODY", length = POST_MAX_CHARS)
    val date = datetime(name = "DATE").default(DateTime())
    val likesRating = integer("LIKES_RATING").default(0)
    val repostCount = integer("REPOST_COUNT").default(0)
    val replyCount = integer("REPLY_COUNT").default(0)
    //the original post that this re-posted
    val originalPostId = long("ORIGINAL_POST_ID").nullable()
}

