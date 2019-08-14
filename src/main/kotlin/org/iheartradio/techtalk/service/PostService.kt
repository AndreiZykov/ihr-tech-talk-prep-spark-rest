package org.iheartradio.techtalk.service

import org.iheartradio.techtalk.SQLStatement
import org.iheartradio.techtalk.domain.dao.*
import org.iheartradio.techtalk.domain.entity.CommentExtrasTable
import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.domain.entity.RepliesTable
import org.iheartradio.techtalk.model.Post
import org.iheartradio.techtalk.utils.APIException
import org.iheartradio.techtalk.utils.ErrorType
import org.iheartradio.techtalk.utils.apiException
import org.iheartradio.techtalk.utils.extensions.execAndMap
import org.iheartradio.techtalk.utils.extensions.paginate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object PostService {

    fun new(post: Post) : Post = transaction {
        val localUser = UserDao.findById(post.userId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
        PostDao.new {
            user = localUser
            body = post.body
            date = DateTime()
            likesRating = 0
            repostCount = 0
            originalPostId = post.originalPost?.id
        }.toPost()
    }


    fun reply(replyToPostId: Long,
              reply: Post) : Post = transaction {

        val localUser = UserDao.findById(reply.userId) ?: throw APIException(ErrorType.USER_NOT_FOUND)

        //Validate that main post (post we are replying to) exists
        val postReplyingTo = PostDao.findById(replyToPostId) ?: apiException(ErrorType.POST_NOT_FOUND)

        //Create Reply Post
        val newReply = PostDao.new {
            user = localUser
            body = reply.body
            date = DateTime()
            likesRating = 0
            repostCount = 0
            originalPostId = null
        }

        //Insert new child relation record
        RepliesDao.new {
            this.postId = postReplyingTo.id.value
            this.replyPostId = newReply.id.value
        }

        //Get count of all replies for this main post (post we are replying to)
        val mainPostReplyCount = RepliesDao
            .find { RepliesTable.postId.eq(replyToPostId) }
            .count()

        //Update the main post's (post we are replying to) reply_count
        postReplyingTo.apply {
            replyCount = mainPostReplyCount
        }

        newReply.toPost()
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