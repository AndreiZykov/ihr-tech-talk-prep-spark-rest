package org.iheartradio.techtalk.controller

import org.eclipse.jetty.http.HttpStatus
import org.iheartradio.techtalk.domain.dao.CommentDao
import org.iheartradio.techtalk.domain.dao.PostDao
import org.iheartradio.techtalk.domain.dao.toComment
import org.iheartradio.techtalk.domain.dao.toPost
import org.iheartradio.techtalk.model.Comment
import org.iheartradio.techtalk.model.response.BaseResponse
import org.iheartradio.techtalk.model.response.ResponseList
import org.iheartradio.techtalk.model.response.ResponseObject
import org.iheartradio.techtalk.service.PostService
import org.iheartradio.techtalk.sparkutils.auth
import org.iheartradio.techtalk.sparkutils.postModel
import org.iheartradio.techtalk.utils.APIException
import org.iheartradio.techtalk.utils.ErrorType
import org.iheartradio.techtalk.utils.toBaseResponse
import org.iheartradio.techtalk.utils.extensions.toJson
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import spark.Route


object PostController {

    val selectById = Route { request, response ->
        val postId = request.params("id").toLong()
        val page: Int = request.queryMap("page").integerValue() ?: 1
        val post = transaction {
            PostDao.findById(postId)?.toPost(page)
        }
        post?.toJson()
    }

    val insertInto = Route { request, response ->
        val authResult = request.auth()

        val post = request.postModel()

        if (authResult.authorizedUserId != post.userId) {
            return@Route BaseResponse.of(ErrorType.FORBIDDEN)
        }

        try {
            ResponseObject(PostService.new(post))
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }

    }


    val insertCommentInto = Route { request, response ->

        val comment = Comment from request.body()

        val newComment = transaction {
            //INSERT new comment record
            val commentDao = CommentDao.new {
                userId = comment.userId
                body = comment.body
                date = DateTime()
                likesCount = 0
                dislikesCount = 0
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



//    val fetchPostsForUser = Route { request, response ->
//        val posts = transaction {
////            PostDao.find {
////                PostsTable.userId eq request.params("userId").toLong()
////            }.map { it.toPost() }
//
//
//            /*
//            val query = Users.innerJoin(UserRatings).innerJoin(StarWarsFilm)
//  .slice(Users.columns)
//  .select {
//    StarWarsFilms.sequelId eq 2 and (UserRatings.value gt 5)
//  }.withDistinct()
//             */
//
////            val query = PostsTable.innerJoin(UsersTable)
////                .slice(PostsTable.columns)
////                .select {
////                    PostsTable.user eq UsersTable
////                }
//
//            PostDao.find {
//                PostsTable.user eq request.params("userId").toLong()
//            }.map { it.toPost() }
//
//
//
//        }
//
//        response.status(200)
//        return@Route posts.toJson()
//    }


//    val fetchPostsForUser = Route { request, response ->
//        val posts = transaction {
//            val userId = request.params("userId").toLong()
////            UserDao[userId]
//
//
//            UserDao.all()
//                .filter { it.id.value == userId }
//                .map { it.posts }
//
//            UserDao.findById(userId)?.posts
//
//        }.map { it.toPost() }
//
//
//        response.status(200)
//        return@Route posts.toJson()
//    }


}