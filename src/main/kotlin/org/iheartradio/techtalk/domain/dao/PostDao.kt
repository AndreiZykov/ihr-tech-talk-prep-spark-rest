package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.shared.Post
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

    companion object : LongEntityClass<PostDao>(PostsTable)
}

fun PostDao.toPost() = Post(
    id = id.value,
    userId = user.id.value,
    body = body,
    date = date,
    likesCount = likesCount,
    commentsCount = commentsCount
)

