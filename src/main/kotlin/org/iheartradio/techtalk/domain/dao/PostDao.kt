package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.model.Post
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

class PostDao(id: EntityID<Long>) : LongEntity(id) {
    var user by UserDao referencedOn PostsTable.user
    var body by PostsTable.body
    var date by PostsTable.date
    var likesRating by PostsTable.likesRating
    var repostCount by PostsTable.repostCount
    var replyCount by PostsTable.replyCount
    var originalPostId by PostsTable.originalPostId
//    var originalPost by PostsTable.originalPost
//    var replies by PostsTable.replies

    companion object : LongEntityClass<PostDao>(PostsTable)
}



fun PostDao.toPost() : Post {
//    val originalPost: Post? = if(originalPostId != null) PostDao.findById(originalPostId!!)?.toPost() else null
    val originalPost: Post? = originalPostId?.let { PostDao.findById(it) }?.toPost()
    return Post(
        id = id.value,
        userId = user.id.value,
        body = body,
        date = date.millis,
        likesRating = likesRating,
        repostCount = repostCount,
        originalPost = originalPost,
        replyCount = replyCount
    )
}