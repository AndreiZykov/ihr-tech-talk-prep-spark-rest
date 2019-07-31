package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.domain.entity.CommentsTable
import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.shared.Comment
import org.iheartradio.techtalk.shared.Post
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

class CommentDao(id: EntityID<Long>) : LongEntity(id) {
    var userId by CommentsTable.userId
    var postId by CommentsTable.postId
    var body by CommentsTable.body
    var date by CommentsTable.date
    var likesCount by CommentsTable.likesCount

    companion object : LongEntityClass<CommentDao>(CommentsTable)
}

fun CommentDao.toComment() = Comment(
    id = id.value,
    userId = userId,
    postId = postId,
    body = body,
    date = date,
    likesCount = likesCount
)

