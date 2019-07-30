package org.iheartradio.techtalk

import org.iheartradio.techtalk.controller.UserController
import org.iheartradio.techtalk.domain.DB
import spark.Spark.*

const val USER_PATH = "/user"
const val POST_PATH = "/post"

fun main() {

    DB.init()

    post("/signIn", UserController.signIn)

    path(USER_PATH) {
        get("/", UserController.fetchAllUsers)
        post("/", UserController.newUser)
        patch("/", UserController.updateUser)
        delete("/", UserController.deleteUser)
        //convenience service to delete all users (for dev mess-ups with references)
        delete("/all", UserController.deleteAllUsers)
    }


//    path(POST_PATH) {
//        get("/{userId}", PostController.fetchPostsForUser)
//    }

}

