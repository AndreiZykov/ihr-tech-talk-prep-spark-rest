package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.domain.dao.UserDao.Companion.referrersOn
import org.iheartradio.techtalk.domain.entity.CommentsTable
import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.model.Post
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

class PostDao(id: EntityID<Long>) : LongEntity(id) {
//    var userId by PostsTable.userId
    var user by UserDao referencedOn PostsTable.user
    var body by PostsTable.body
    var date by PostsTable.date
    var likesCount by PostsTable.likesCount
    var commentsCount by PostsTable.commentsCount
    val comments by CommentDao referrersOn CommentsTable.post

    companion object : LongEntityClass<PostDao>(PostsTable)
}

fun PostDao.toPost(page : Int = 1) = Post(
    id = id.value,
    userId = user.id.value,
    body = body,
    date = date.millis,
    likesCount = likesCount,
    commentsCount = commentsCount,
    comments = comments
        .take(page * 2)
        .map { it.toComment() }
)

