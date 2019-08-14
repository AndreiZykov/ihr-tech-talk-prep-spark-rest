package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.SQLStatement
import org.iheartradio.techtalk.domain.entity.CommentsTable
import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.domain.entity.RepliesTable
import org.iheartradio.techtalk.model.Post
import org.iheartradio.techtalk.utils.extensions.execAndMap
import org.iheartradio.techtalk.utils.extensions.paginate
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLData

class PostDao(id: EntityID<Long>) : LongEntity(id) {
    var user by UserDao referencedOn PostsTable.user
    var body by PostsTable.body
    var date by PostsTable.date
    var likesRating by PostsTable.likesRating
    var repostCount by PostsTable.repostCount
    var replyCount by PostsTable.replyCount
    var originalPostId by PostsTable.originalPostId
//    var replies by PostsTable.replies

    companion object : LongEntityClass<PostDao>(PostsTable)
}


fun PostDao.toPost() = Post(
    id = id.value,
    userId = user.id.value,
    body = body,
    date = date.millis,
    likesRating = likesRating,
    repostCount = repostCount,
    originalPost = null,
    replyCount = replyCount
)