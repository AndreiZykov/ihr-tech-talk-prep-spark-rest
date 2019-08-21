package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.domain.entity.PostExtrasTable
import org.iheartradio.techtalk.model.PostExtras
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

class PostExtrasDao(id: EntityID<Long>) : LongEntity(id) {
    var userId by PostExtrasTable.userId
    var postId by PostExtrasTable.postId
    var like by PostExtrasTable.like
    var repost by PostExtrasTable.repost

    fun updateLike() {
        like = if(like <= 0) 1 else 0
    }

    fun updateDislike() {
        like = if(like >= 0) -1 else 0
    }

    fun updateRepost() {
        repost = if(repost > 0) 0 else 1
    }

    companion object : LongEntityClass<PostExtrasDao>(PostExtrasTable)
}

fun PostExtrasDao.toPostExtra() = PostExtras(
    id = id.value,
    userId = userId,
    postId = postId,
    like = like,
    repost = repost
)
