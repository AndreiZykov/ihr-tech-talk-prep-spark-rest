package org.iheartradio.techtalk

import org.iheartradio.techtalk.controller.PostController
import org.iheartradio.techtalk.controller.UserController
import org.iheartradio.techtalk.domain.DB
import spark.Spark.*

const val USER_PATH = "/user"
const val POST_PATH = "/post"

fun main(args: Array<String>) {

    port(herokuPort())

    DB.init()

    get("/") { req, res ->
        res.status(200)
        "Hello World"
    }

    post("/signIn", UserController.signIn)

    path(USER_PATH) {
        get("/", UserController.fetchAllUsers)
        post("/", UserController.newUser)
        patch("/", UserController.updateUser)
        delete("/", UserController.deleteUser)
        //convenience service to delete all users (for dev mess-ups with references)
        delete("/all", UserController.deleteAllUsers)
    }



    path(POST_PATH) {
        /**
         * Maybe change this to   [localhost:4567/user/:id/post]
         */
        get("", PostController.fetchAllPosts)

        /**
         * Maybe change this to   [localhost:4567/post]
         */
        post("", PostController.newPost)
    }

}

fun herokuPort(): Int {
    val processBuilder = ProcessBuilder()
    if (processBuilder.environment()["PORT"] != null) {
        return Integer.parseInt(processBuilder.environment()["PORT"])
    }
    return 4567
}

