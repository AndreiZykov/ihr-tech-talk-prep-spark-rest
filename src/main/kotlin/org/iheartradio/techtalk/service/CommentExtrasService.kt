package org.iheartradio.techtalk.service

import org.iheartradio.techtalk.domain.dao.CommentDao
import org.iheartradio.techtalk.domain.dao.CommentExtrasDao
import org.iheartradio.techtalk.domain.dao.toCommentExtra
import org.iheartradio.techtalk.model.CommentExtras
import org.iheartradio.techtalk.utils.ErrorType
import org.iheartradio.techtalk.utils.apiException
import org.jetbrains.exposed.sql.transactions.transaction

object CommentExtrasService {

    fun new(extras: CommentExtras) : CommentExtras = transaction {
        CommentExtrasDao.findById(extras.userId) ?: apiException(ErrorType.USER_NOT_FOUND)
        CommentDao.findById(extras.commentId) ?: apiException(ErrorType.COMMENT_NOT_FOUND)
        CommentExtrasDao.new {
            userId = extras.userId
            commentId = extras.commentId
            like = extras.like
            dislike = extras.dislike
            repost = extras.repost
            share = extras.share
        }.toCommentExtra()
    }
}