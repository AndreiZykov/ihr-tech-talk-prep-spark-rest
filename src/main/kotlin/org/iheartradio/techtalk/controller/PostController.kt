package org.iheartradio.techtalk.controller

object PostController {

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


//    val newUser = Route { request, response ->
//        val post = Post from request.body()
//
//        /*
//            var user by UserDao referencedOn PostsTable.user
//    var body by PostsTable.body
//    var date by PostsTable.date
//    var likesCount by PostsTable.likesCount
//    var commentsCount by PostsTable.commentsCount
//         */
//
//        val newUser = transaction {
//            PostDao.new {
//                body = ""
//            }
//        }.toUser()
//
//        response.status(HttpStatus.OK_200)
//
//        return@Route newUser.toJson()
//    }


    /*
    val newUser = Route { request, response ->
        val user = User from request.body()

        val newUser = transaction {
            UserDao.new {
                username = user.username
                password_hash = hasher(user.password).create()
            }
        }.toUser()

        response.status(HttpStatus.OK_200)

        return@Route newUser.toJson()
    }

     */
}