package org.iheartradio.techtalk.controller

import org.eclipse.jetty.http.HttpStatus
import org.iheartradio.techtalk.domain.dao.*
import org.iheartradio.techtalk.model.Comment
import org.iheartradio.techtalk.model.Post
import org.iheartradio.techtalk.model.response.BaseResponse
import org.iheartradio.techtalk.model.response.ResponseList
import org.iheartradio.techtalk.model.response.ResponseObject
import org.iheartradio.techtalk.service.PostService
import org.iheartradio.techtalk.sparkutils.auth
import org.iheartradio.techtalk.sparkutils.postModel
import org.iheartradio.techtalk.utils.APIException
import org.iheartradio.techtalk.utils.ErrorType
import org.iheartradio.techtalk.utils.apiException
import org.iheartradio.techtalk.utils.toBaseResponse
import org.iheartradio.techtalk.utils.extensions.toJson
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import spark.Route


object PostController {

    val selectById = Route { request, _ ->
        val postId = request.params("id").toLong()
        //TODO: Removing paging from this call. We will be fetching the replies with another endpoint/call
        val page: Int = request.queryMap("page").integerValue() ?: 1
        val post = transaction {
            PostDao.findById(postId)?.toPost()
        }
        post?.toJson()
    }

//    val insertInto = Route { request, _ ->
//        val post = request.postModel()
//        runCatching { PostService.new(post) }
//            .fold(
//                { newPost -> ResponseObject(newPost) },
//                { e ->
//                    return@Route (e as? APIException)?.toBaseResponse() ?: BaseResponse.of(ErrorType.SERVER_ERROR)
//                })
//            .takeIf { request.auth().authorizedUserId == post.userId }
//            ?: BaseResponse.of(ErrorType.FORBIDDEN)
//    }


    val insertInto = Route { request, _ ->
        val post = request.postModel()
        ResponseObject(PostService.new(post))
            .takeIf { request.auth().authorizedUserId == post.userId }
            ?: BaseResponse.of(ErrorType.FORBIDDEN)
    }


    val insertReply = Route { request, _ ->
        val reply = request.postModel()
        val replyToPostId = request.params("id").toLong()
        ResponseObject(PostService.reply(replyToPostId, reply))
            .takeIf { request.auth().authorizedUserId == reply.userId }
            ?: BaseResponse.of(ErrorType.FORBIDDEN)
    }

    val insertCommentInto = Route { request, response ->

        val comment = Comment from request.body()

        val newComment = transaction {
            //INSERT new comment record
            val commentDao = CommentDao.new {
                userId = comment.userId
                body = comment.body
                date = DateTime()
                likeRating = 0
                repostCount = 0
                shareCount = 0
                post = PostDao.findById(comment.postId)!!
            }

            /*

            //Get total count of comments for this post
            val totalComments = CommentDao.all()
                .filter { it.postId == comment.postId }
                .size

            //UPDATE Post table record commentsCount to equal totalComments
            PostDao.findById(comment.postId)
                ?.apply { commentsCount = totalComments }

            */


//            PostsTable.update ({
//                PostsTable.id eq request.params("userId").toLong()
//            }) {
//                with(SqlExpressionBuilder) {
//                    it.update(PostsTable.commentsCount, totalComments)
//                }
//            }

            //Map CommentDao to Comment data class and then set response status to OK_200 if entire transaction succeeds
            commentDao.toComment().also {
                response.status(HttpStatus.OK_200)
            }
        }

        return@Route newComment.toJson()
    }


    val feed = Route { request, response ->
        return@Route try {
            val page: Int = request.queryMap("page").integerValue() ?: 1
            ResponseList(PostService.fetchFeed(page))
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }


    val insertRepost = Route { request, _ ->
        val originalPostId = request.params("id").toLong()
        val post = request.postModel().apply {
            originalPost = PostDao.findById(originalPostId)?.toPost()
        }
        runCatching { PostService.new(post) }
            .fold(
                { newPost -> ResponseObject(newPost) },
                { e ->
                    return@Route (e as? APIException)?.toBaseResponse() ?: BaseResponse.of(ErrorType.SERVER_ERROR)
                })
            .takeIf { request.auth().authorizedUserId == post.userId }
            ?: BaseResponse.of(ErrorType.FORBIDDEN)
    }


}