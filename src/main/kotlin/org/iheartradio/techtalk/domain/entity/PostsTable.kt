package org.iheartradio.techtalk.domain.entity

import org.iheartradio.techtalk.domain.entity.UsersTable.nullable
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.or
import org.joda.time.DateTime
import java.math.BigDecimal

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
    //the post that this post was quoted from
    val quotedPostId = long("QUOTED_POST_ID").nullable()
    //the post id that this post is reply to, temp solution
    val repliedPostId = long("REPLIED_POST_ID").nullable()

    val geoLatitude = decimal(name = "GEO_LATITUDE",
        precision = 20,
        scale = 15)
        .default(defaultValue = BigDecimal.ZERO)

    val geoLongitude = decimal(name = "GEO_LONGITUDE",
        precision = 20,
        scale = 15)
        .default(defaultValue = BigDecimal.ZERO)


}

