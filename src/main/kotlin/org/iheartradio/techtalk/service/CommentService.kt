package org.iheartradio.techtalk.service

import org.iheartradio.techtalk.domain.dao.CommentDao
import org.iheartradio.techtalk.domain.dao.CommentExtrasDao
import org.iheartradio.techtalk.domain.entity.CommentExtrasTable
import org.iheartradio.techtalk.model.Comment
import org.iheartradio.techtalk.model.CommentExtras
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object CommentService {

    fun delete(comment: Comment) {
        transaction { CommentDao.findById(comment.id)?.delete() }
    }


    fun dislike(userId: Long, commentId: Long) {
        transaction {

            val extras = CommentExtrasService.find(userId, commentId)

            if(extras != null) { //recordExists
                CommentExtrasDao.findById(extras.id)?.updateDislike()
            } else {
                CommentExtrasService.new(CommentExtras(
                    userId = userId,
                    commentId = commentId,
                    like = -1
                ))
            }

            val totalLikes = CommentExtrasDao
                .find { CommentExtrasTable.userId.eq(userId) and CommentExtrasTable.like.greater(0) }
                .count()

            val totalDislike = CommentExtrasDao
                .find { CommentExtrasTable.userId.eq(userId) and CommentExtrasTable.like.less(0) }
                .count()

            CommentDao.findById(commentId)?.apply {
                likeRating = totalLikes - totalDislike
            }
        }
    }

    fun like(userId: Long, commentId: Long) {
        transaction {

            val extras = CommentExtrasService.find(userId, commentId)

            if(extras != null) { //recordExists
                CommentExtrasDao.findById(extras.id)?.updateLike()
            } else {
                CommentExtrasService.new(CommentExtras(
                    userId = userId,
                    commentId = commentId,
                    like = 1
                ))
            }

            val totalLikes = CommentExtrasDao
                .find { CommentExtrasTable.userId.eq(userId) and CommentExtrasTable.like.greater(0) }
                .count()

            val totalDislike = CommentExtrasDao
                .find { CommentExtrasTable.userId.eq(userId) and CommentExtrasTable.like.less(0) }
                .count()

            CommentDao.findById(commentId)?.apply {
                likeRating = totalLikes - totalDislike
            }
        }
    }


    fun update(comment: Comment) {
        transaction { CommentDao.findById(comment.id)?.apply {
            body = comment.body
        } }
    }

}