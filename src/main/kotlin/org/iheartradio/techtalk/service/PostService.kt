package org.iheartradio.techtalk.service

import org.iheartradio.techtalk.domain.dao.PostDao
import org.iheartradio.techtalk.domain.dao.UserDao
import org.iheartradio.techtalk.domain.dao.toPost
import org.iheartradio.techtalk.model.Post
import org.iheartradio.techtalk.utils.APIException
import org.iheartradio.techtalk.utils.ErrorType
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object PostService {
    fun new(post: Post) : Post {
        return transaction {
            val localUser = UserDao.findById(post.userId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
            PostDao.new {
                user = localUser
                body = post.body
                date = DateTime()
                likesCount = 0
                commentsCount = 0
            }.toPost()
        }
    }
}