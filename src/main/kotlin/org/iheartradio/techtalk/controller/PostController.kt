package org.iheartradio.techtalk.controller

import org.eclipse.jetty.http.HttpStatus
import org.iheartradio.techtalk.domain.dao.PostDao
import org.iheartradio.techtalk.domain.dao.UserDao
import org.iheartradio.techtalk.domain.dao.toPost
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
                date = DateTime(post.date)
                likesCount = 0
                commentsCount = 0
            }.toPost()
        }

        response.status(HttpStatus.OK_200)

        return@Route newPost.toJson()
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