package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.domain.entity.CommentExtrasTable
import org.iheartradio.techtalk.model.CommentExtras
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

class CommentExtrasDao(id: EntityID<Long>) : LongEntity(id) {
    var userId by CommentExtrasTable.userId
    var commentId by CommentExtrasTable.commentId
    var like by CommentExtrasTable.like
    var dislike by CommentExtrasTable.dislike
    var repost by CommentExtrasTable.repost
    var share by CommentExtrasTable.share

    fun updateLike() {
        like = if(like == 0) 1 else 0
    }

    fun updateDislike() {
        dislike = if(dislike == 0) 1 else 0
    }

    companion object : LongEntityClass<CommentExtrasDao>(CommentExtrasTable)
}

fun CommentExtrasDao.toCommentExtra() = CommentExtras(
    id = id.value,
    userId = userId,
    commentId = commentId,
    like = like,
    dislike = dislike,
    repost = repost,
    share = share
)
