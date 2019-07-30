package org.iheartradio.techtalk.domain.entity

import org.jetbrains.exposed.dao.LongIdTable
import org.joda.time.DateTime

const val POST_MAX_CHARS = 500

object PostsTable : LongIdTable() {
//    val userId = long("USER_ID")
    val user = reference("USER", UsersTable)
    val body = varchar(name = "BODY", length = POST_MAX_CHARS)
    val date = datetime(name = "DATE").default(DateTime())
    val likesCount = integer("LIKES_COUNT").default(0)
    val commentsCount = integer("COMMENTS_COUNT").default(0)
}