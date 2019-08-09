package org.iheartradio.techtalk.service

import org.iheartradio.techtalk.domain.dao.PostDao
import org.iheartradio.techtalk.domain.dao.UserDao
import org.iheartradio.techtalk.domain.dao.toPost
import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.model.Post
import org.iheartradio.techtalk.utils.APIException
import org.iheartradio.techtalk.utils.ErrorType
import org.iheartradio.techtalk.utils.extensions.paginate
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object PostService {
    fun new(post: Post) : Post = transaction {
        val localUser = UserDao.findById(post.userId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
        PostDao.new {
            user = localUser
            body = post.body
            date = DateTime()
            likesCount = 0
            commentsCount = 0
        }.toPost()
    }

    fun allByUserId(userId: Long) : List<Post> {
        return transaction {
            val localUser = UserDao.findById(userId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
            localUser.posts.map { it.toPost() }
        }
    }


    fun fetchFeed(page : Int = 1,
                  pageItemCount: Int = 5) = transaction {
        PostDao.all()
            .orderBy(PostsTable.date to SortOrder.DESC)
            .paginate(page, pageItemCount)
            .map { it.toPost() }
    }


}