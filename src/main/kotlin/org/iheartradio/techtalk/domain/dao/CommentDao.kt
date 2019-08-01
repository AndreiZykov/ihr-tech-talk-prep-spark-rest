package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.domain.entity.CommentsTable
import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.model.Comment
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

class CommentDao(id: EntityID<Long>) : LongEntity(id) {
    var userId by CommentsTable.userId
    var body by CommentsTable.body
    var date by CommentsTable.date
    var likesCount by CommentsTable.likesCount
    var dislikesCount by CommentsTable.dislikesCount
    var post by PostDao referencedOn CommentsTable.post
        //var user by UserDao referencedOn PostsTable.user
    companion object : LongEntityClass<CommentDao>(CommentsTable)
}

fun CommentDao.toComment() = Comment(
    id = id.value,
    userId = userId,
    postId = post.id.value,
    body = body,
    date = date.millis,
    likesCount = likesCount,
    dislikesCount = dislikesCount
)

