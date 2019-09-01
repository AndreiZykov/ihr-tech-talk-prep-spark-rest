package org.iheartradio.techtalk.service

import org.iheartradio.techtalk.controller.deleteAll
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

    fun new(post: Post): Post = transaction {
        val localUser = UserDao.findById(post.userId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
        PostDao.new {
            user = localUser
            body = post.body
            date = DateTime()
            likesRating = 0
            repostCount = 0
            originalPostId = post.originalPost?.id
        }.toPost(localUser.id.value)
    }



//    fun repost(post: Post, originalPostId: Long): Post = transaction {
//        val localUser = UserDao.findById(post.userId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
//        val originalPost: Post = PostDao.findById(originalPostId)?.toPost() ?: apiException(ErrorType.POST_NOT_FOUND)
//        val newPost = PostDao.new {
//            user = localUser
//            body = post.body
//            date = DateTime()
//            likesRating = 0
//            repostCount = 0
//            this.originalPostId = originalPost.id
//        }.toPost()
//
//        val localUserId = localUser.id.value
//
//        val extras = PostExtrasService.find(localUserId, originalPost.id)
//
//        if (extras != null) {
//            PostExtrasDao.findById(extras.id)?.updateRepost()
//        } else {
//            PostExtrasService.new(
//                PostExtras(
//                    userId = localUserId,
//                    postId = originalPost.id,
//                    repost = 1
//                )
//            )
//        }
//
//        val totalReposts = PostExtrasDao
//            .find { PostExtrasTable.postId.eq(originalPost.id) and PostExtrasTable.repost.greater(0) }
//            .count()
//
//
//        PostDao.findById(originalPost.userId)?.apply {
//            repostCount = totalReposts
//        }
//
//        newPost
//    }


    fun repost(localUserId: Long, originalPostId: Long): Post? = transaction {
        val localUser = UserDao.findById(localUserId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
        val originalPost: Post = PostDao.findById(originalPostId)?.toPost(localUser.id.value) ?: apiException(ErrorType.POST_NOT_FOUND)
//        val alreadyReposted = PostDao.find {
//            PostsTable.originalPostId.eq(originalPostId) and PostsTable.user.eq(localUserId)
//        }.count() > 0




//        val newPost = PostDao.new {
//            user = localUser
//            body = originalPost.body
//            date = DateTime()
//            likesRating = 0
//            repostCount = 0
//            this.originalPostId = originalPost.id
//        }.toPost(localUser.id.value)

        var newPost: Post? = null

        val extras = PostExtrasService.find(localUserId, originalPost.id)

        if (extras != null) {
//            PostExtrasDao.findById(extras.id)?.updateRepost()

            val updatedExtras = PostExtrasDao.findById(extras.id)?.apply {
                repost = if(repost > 0) 0 else 1
            }

            if(updatedExtras?.repost == 1) {
                //insert new post with original post details
                newPost  = PostDao.new {
                    user = localUser
                    body = originalPost.body
                    date = DateTime()
                    likesRating = 0
                    repostCount = 0
                    this.originalPostId = originalPost.id
                }.toPost(localUser.id.value)
            } else {
                //delete the re-posted post
                PostDao
                    .find { PostsTable.originalPostId.eq(originalPost.id) and  PostsTable.user.eq(localUser.id) }
                    .firstOrNull()
                    ?.delete()
            }

        } else {
            PostExtrasService.new(
                PostExtras(
                    userId = localUserId,
                    postId = originalPost.id,
                    repost = 1
                )
            )
        }

        val totalReposts = PostExtrasDao
            .find { PostExtrasTable.postId.eq(originalPost.id) and PostExtrasTable.repost.greater(0) }
            .count()


        PostDao.findById(originalPost.id)?.apply {
            repostCount = totalReposts
        }

        newPost
    }


    fun quote(post: Post, quotedPostId: Long): Post = transaction {
        val localUser = UserDao.findById(post.userId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
        val quotedPost: Post = PostDao.findById(quotedPostId)?.toPost(localUser.id.value) ?: apiException(ErrorType.POST_NOT_FOUND)
        PostDao.new {
            user = localUser
            body = post.body
            date = DateTime()
            likesRating = 0
            repostCount = 0
            this.quotedPostId = quotedPost.id
        }.toPost(localUser.id.value)
    }

    fun dislike(userId: Long, postId: Long): Post = transaction {

        val post = PostDao.findById(postId) ?: apiException(ErrorType.POST_NOT_FOUND)

        val extras = PostExtrasService.find(userId, postId)

        if (extras != null) { //recordExists
            PostExtrasDao.findById(extras.id)?.updateDislike()
        } else {
            PostExtrasService.new(
                PostExtras(
                    userId = userId,
                    postId = postId,
                    rating = -1
                )
            )
        }

        val totalLikes = PostExtrasDao
//                .find { PostExtrasTable.userId.eq(userId) and PostExtrasTable.like.greater(0) }
            .find { PostExtrasTable.postId.eq(postId) and PostExtrasTable.like.greater(0) }
            .count()

        val totalDislike = PostExtrasDao
            .find { PostExtrasTable.postId.eq(postId) and PostExtrasTable.like.less(0) }
            .count()

        post.apply {
            likesRating = totalLikes - totalDislike

        }.toPost(userId)
    }

//    fun like(userId: Long, postId: Long) {
//        transaction {
//
//            val post = PostDao.findById(postId) ?: apiException(ErrorType.POST_NOT_FOUND)
//
//            val extras = PostExtrasService.find(userId, postId)
//
//            if (extras != null) { //recordExists
//                PostExtrasDao.findById(extras.id)?.updateLike()
//            } else {
//                PostExtrasService.new(
//                    PostExtras(
//                        userId = userId,
//                        postId = postId,
//                        like = 1
//                    )
//                )
//            }
//
//            val totalLikes = PostExtrasDao
//                .find { PostExtrasTable.postId.eq(postId) and PostExtrasTable.like.greater(0) }
//                .count()
//
//            val totalDislike = PostExtrasDao
//                .find { PostExtrasTable.postId.eq(postId) and PostExtrasTable.like.less(0) }
//                .count()
//
//            post.apply {
//                likesRating = totalLikes - totalDislike
//            }
//        }
//    }



    fun like(userId: Long, postId: Long): Post =  transaction {

        val post = PostDao.findById(postId) ?: apiException(ErrorType.POST_NOT_FOUND)

        val extras = PostExtrasService.find(userId, postId)

        if (extras != null) { //recordExists
            PostExtrasDao.findById(extras.id)?.updateLike()
        } else {
            PostExtrasService.new(
                PostExtras(
                    userId = userId,
                    postId = postId,
                    rating = 1
                )
            )
        }

        val totalLikes = PostExtrasDao
            .find { PostExtrasTable.postId.eq(postId) and PostExtrasTable.like.greater(0) }
            .count()

        val totalDislike = PostExtrasDao
            .find { PostExtrasTable.postId.eq(postId) and PostExtrasTable.like.less(0) }
            .count()

        post.apply {
            likesRating = totalLikes - totalDislike
        }.toPost(userId)
    }

    fun reply(
        replyToPostId: Long,
        reply: Post
    ): Post = transaction {

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

        newReply.toPost(localUser.id.value)
    }

    fun allByUserId(localUserId: Long): List<Post> {
        return transaction {
            val localUser = UserDao.findById(localUserId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
            localUser.posts.map { it.toPost(localUser.id.value) }
        }
    }


    fun fetchFeed(localUserId: Long,
                  page : Int = 1,
                  pageItemCount: Int = 10) = transaction {
        println("DEBUG:: fetchFeed called for $localUserId")
        PostDao.all()
            .orderBy(PostsTable.date to SortOrder.DESC)
            .paginate(page, pageItemCount)
            .map { it.toPost(localUserId) }
    }


}