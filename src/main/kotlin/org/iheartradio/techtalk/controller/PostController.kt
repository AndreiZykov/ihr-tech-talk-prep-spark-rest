package org.iheartradio.techtalk.controller

import org.eclipse.jetty.http.HttpStatus
import org.iheartradio.techtalk.domain.dao.PostDao
import org.iheartradio.techtalk.domain.dao.UserDao
import org.iheartradio.techtalk.domain.dao.toPost
import org.iheartradio.techtalk.shared.toJson
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Route



object PostController {

    /**
     * If the endpoint url contains ?userId={id} then this will fetch
     * all posts for a specific user
     *
     * If there is no query parameter for ?userId={id} then it will return
     * all posts across all users
     */
    val fetchAllPosts = Route { request, response ->
        val userId: Long? = request.queryMap("userId").longValue()
        val posts = transaction {
            val allPosts = PostDao.all()
            if (userId == null) {
                allPosts.map { it.toPost() }
            } else {
                allPosts.filter { it.user.id.value == userId }.map { it.toPost() }
            }
        }

        response.status(HttpStatus.OK_200)
        return@Route posts.toJson()
    }


    /**
     * Must pass query parameter for ?userId={id} to create post
     * for a specific user.
     */
    val newPost = Route { request, response ->

        val userId = request.queryMap("userId").longValue()
            ?: throw Exception("Query @param [userId] not found in the query map! Please pass over ?userId={id} and try again!")

        val newPost = transaction {

            val user = UserDao.findById(userId) ?: throw Exception("INVALID USER_ID. No User found with id= $userId")

            PostDao.new {
                body = "VERY NICE!! BEST POST EVER!"
                this.user = user
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