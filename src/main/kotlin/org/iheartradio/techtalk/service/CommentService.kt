package org.iheartradio.techtalk.service

import org.iheartradio.techtalk.domain.dao.CommentDao
import org.iheartradio.techtalk.domain.dao.CommentExtrasDao
import org.iheartradio.techtalk.domain.entity.CommentExtrasTable
import org.iheartradio.techtalk.model.Comment
import org.iheartradio.techtalk.model.CommentExtras
import org.jetbrains.exposed.sql.transactions.transaction

object CommentService {

    fun delete(comment: Comment) {
        transaction { CommentDao.findById(comment.id)?.delete() }
    }

    fun update(comment: Comment) {
        transaction { CommentDao.findById(comment.id)?.apply {
            body = comment.body
        } }
    }


    fun like(userId: Long,
             commentId: Long) {
        transaction {
         //   CommentDao.findById(comment.id)?.delete()
            val totalLikes = CommentExtrasDao
                .find { CommentExtrasTable.id eq commentId }
                .sumBy { it.like }
                .plus(1)

            CommentExtrasService.new(CommentExtras(
                userId = userId,
                commentId = commentId,
                like = 1
            ))

        }
    }
}