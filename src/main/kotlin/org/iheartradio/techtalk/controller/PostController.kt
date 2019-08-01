package org.iheartradio.techtalk.controller

import org.eclipse.jetty.http.HttpStatus
import org.iheartradio.techtalk.domain.dao.*
import org.iheartradio.techtalk.model.Comment
import org.iheartradio.techtalk.model.Post
import org.iheartradio.techtalk.model.toJson
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import spark.Route


object PostController {

    val insertInto = Route { request, response ->
        val post = Post from request.body()
        val userId = post.userId
        val newPost = transaction {

            val localUser = UserDao.findById(userId)
                ?: throw Exception("Invalid {userId}. No User found with id= $userId")

            PostDao.new {
                user = localUser
                body = post.body
                date = DateTime()
                likesCount = 0
                commentsCount = 0
            }.toPost()
        }

        response.status(HttpStatus.OK_200)

        return@Route newPost.toJson()
    }



    val insertCommentInto = Route { request, response ->

        val comment = Comment from request.body()

        val newComment = transaction {
            //INSERT new comment record
            val commentDao = CommentDao.new {
                userId = comment.userId
                postId = comment.postId
                body = comment.body
                date = DateTime()
                likesCount = 0
                dislikesCount = 0
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