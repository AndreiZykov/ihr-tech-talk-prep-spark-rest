package org.iheartradio.techtalk

import org.iheartradio.techtalk.controller.CommentController
import org.iheartradio.techtalk.controller.CommentExtrasController
import org.iheartradio.techtalk.controller.PostController
import org.iheartradio.techtalk.controller.UserController
import org.iheartradio.techtalk.domain.DB
import org.iheartradio.techtalk.model.Comment
import org.iheartradio.techtalk.sparkutils.delete
import org.iheartradio.techtalk.sparkutils.get
import org.iheartradio.techtalk.sparkutils.patch
import org.iheartradio.techtalk.sparkutils.post
import org.iheartradio.techtalk.utils.extensions.toJson
import spark.Spark.*

const val DEFAULT_PORT = 4567
const val CONTENT_TYPE = "application/json"
const val KEY_POST_ENV_VAR = "PORT"

//endpoint base paths
const val USER_PATH = "/user"
const val POST_PATH = "/post"
const val COMMENT_PATH = "/comment"
const val FEED_PATH = "/feed"

//DEV-TESTING-ENDPOINTS
const val COMMENT_EXTRAS_PATH = "/commentExtras"

//endpoint path actions
const val LIKE = "like"
const val DISLIKE = "dislike"


fun main(args: Array<String>) {

    port(herokuPort)

    DB.init()

    get("/") { req, res ->
        "Hello World"
    }

    post("/signIn", UserController.signIn)

    path(USER_PATH) {
        get(UserController.selectAll)
        post(UserController.insertInto)
        patch(UserController.update)
        delete(UserController.delete)
        //convenience service to delete all users (for dev mess-ups with references)
        delete("/all", UserController.deleteAll)
        get("/:id/posts", UserController.selectPostsByUser)
    }

    path(POST_PATH) {
        get("/:id", PostController.selectById)
        post(PostController.insertInto)
        post("/:id/comment", PostController.insertCommentInto)
        post("/:id/reply", PostController.insertReply)
    }


    path(FEED_PATH) {
        get(PostController.feed)
    }


    path(COMMENT_PATH) {
        post("/:id/$LIKE", CommentController.like)
        post("/:id/$DISLIKE", CommentController.dislike)
    }

    path(COMMENT_EXTRAS_PATH) {
        get(CommentExtrasController.selectAll)
    }

    afterAfter { _, response ->
        response.type(CONTENT_TYPE)
        // temporary for react
        response.header("Access-Control-Allow-Origin", "http://localhost:3000")
        response.header("Access-Control-Allow-Credentials", "true");
    }

}

private val herokuPort = ProcessBuilder()
    .let { it.environment()[KEY_POST_ENV_VAR] }
    .takeIf { port -> port != null }
    ?.let { Integer.parseInt(it) }
    ?: DEFAULT_PORT


//private val herokuPort = DEFAULT_PORT