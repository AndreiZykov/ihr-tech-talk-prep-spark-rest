package org.iheartradio.techtalk.service

import org.iheartradio.techtalk.domain.dao.CommentDao
import org.iheartradio.techtalk.model.Comment
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
}