package org.iheartradio.techtalk.service

import org.iheartradio.techtalk.domain.dao.*
import org.iheartradio.techtalk.domain.entity.PostExtrasTable
import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.domain.entity.RepliesTable
import org.iheartradio.techtalk.model.PostExtras
import org.iheartradio.techtalk.model.Post
import org.iheartradio.techtalk.utils.APIException
import org.iheartradio.techtalk.utils.ErrorType
import org.iheartradio.techtalk.utils.apiException
import org.iheartradio.techtalk.utils.extensions.paginate
import org.jetbrains.exposed.sql.*
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


    fun dislike(userId: Long, postId: Long) {
        transaction {

            val extras = PostExtrasService.find(userId, postId)

            if(extras != null) { //recordExists
                PostExtrasDao.findById(extras.id)?.updateDislike()
            } else {
                PostExtrasService.new(
                    PostExtras(
                        userId = userId,
                        postId = postId,
                        like = -1
                    )
                )
            }

            val totalLikes = PostExtrasDao
                .find { PostExtrasTable.userId.eq(userId) and PostExtrasTable.like.greater(0) }
                .count()

            val totalDislike = PostExtrasDao
                .find { PostExtrasTable.userId.eq(userId) and PostExtrasTable.like.less(0) }
                .count()

            PostDao.findById(postId)?.apply {
                likesRating = totalLikes - totalDislike
            }
        }
    }

    fun like(userId: Long, postId: Long) {
        transaction {

            val extras = PostExtrasService.find(userId, postId)

            if(extras != null) { //recordExists
                PostExtrasDao.findById(extras.id)?.updateLike()
            } else {
                PostExtrasService.new(
                    PostExtras(
                        userId = userId,
                        postId = postId,
                        like = 1
                    )
                )
            }

            val totalLikes = PostExtrasDao
                .find { PostExtrasTable.userId.eq(userId) and PostExtrasTable.like.greater(0) }
                .count()

            val totalDislike = PostExtrasDao
                .find { PostExtrasTable.userId.eq(userId) and PostExtrasTable.like.less(0) }
                .count()

            PostDao.findById(postId)?.apply {
                likesRating = totalLikes - totalDislike
            }
        }
    }

    /*
      fun repost(userId: Long, commentId: Long) {
        transaction {

            val extras = PostExtrasService.find(userId, commentId)

            if(extras != null) { //recordExists
                PostExtrasDao.findById(extras.id)?.updateLike()
            } else {
                PostExtrasService.new(PostExtras(
                    userId = userId,
                    commentId = commentId,
                    like = 1
                ))
            }

            val totalLikes = PostExtrasDao
                .find { PostExtrasTable.userId.eq(userId) and PostExtrasTable.like.greater(0) }
                .count()

            val totalDislike = PostExtrasDao
                .find { PostExtrasTable.userId.eq(userId) and PostExtrasTable.like.less(0) }
                .count()

            CommentDao.findById(commentId)?.apply {
                likeRating = totalLikes - totalDislike
            }
        }
    }
     */

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
                  pageItemCount: Int = 10) = transaction {
        PostDao.all()
            .orderBy(PostsTable.date to SortOrder.DESC)
            .paginate(page, pageItemCount)
            .map { it.toPost() }
    }


}