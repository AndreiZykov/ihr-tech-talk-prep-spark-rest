package org.iheartradio.techtalk

import org.iheartradio.techtalk.controller.PostController
import org.iheartradio.techtalk.controller.UserController
import org.iheartradio.techtalk.domain.DB
import org.iheartradio.techtalk.model.Comment
import org.iheartradio.techtalk.sparkutils.post
import org.iheartradio.techtalk.utils.extensions.toJson
import spark.Spark.*

const val DEFAULT_PORT = 4567
const val CONTENT_TYPE = "application/json"
const val KEY_POST_ENV_VAR = "PORT"
const val USER_PATH = "/user"
const val POST_PATH = "/post"
const val COMMENT_PATH = "/comment"
const val FEED = "/feed"


fun main(args: Array<String>) {

    println(
        Comment(
            userId = 3,
            postId = 20,
            body = "THIS POST SUCKS!"
        ).toJson()
    )

    port(herokuPort)

    DB.init()

    get("/") { req, res ->
        "Hello World"
    }

    post("/signIn", UserController.signIn)

    path(USER_PATH) {
        get("/", UserController.selectAll)
        post("/", UserController.insertInto)
        patch("/", UserController.update)
        delete("/", UserController.delete)
        //convenience service to delete all users (for dev mess-ups with references)
        delete("/all", UserController.deleteAll)
        get("/:id/posts", UserController.selectPostsByUser)
    }

    path(POST_PATH) {
        get("/:id", PostController.selectById)
        post(PostController.insertInto)
        post("/:id/comment", PostController.insertCommentInto)
    }

    path(COMMENT_PATH) {

    }

    path(FEED) {
        get("/", PostController.feed)
    }

    afterAfter { _ , response -> response.type(CONTENT_TYPE) }

}

private val herokuPort = ProcessBuilder()
    .let { it.environment()[KEY_POST_ENV_VAR] }
    .takeIf { port -> port != null }
    ?.let { Integer.parseInt(it) }
    ?: DEFAULT_PORT


//private val herokuPort = DEFAULT_PORT