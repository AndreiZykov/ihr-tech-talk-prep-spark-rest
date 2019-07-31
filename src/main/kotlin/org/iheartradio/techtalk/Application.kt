package org.iheartradio.techtalk

import org.eclipse.jetty.http.HttpStatus
import org.iheartradio.techtalk.controller.PostController
import org.iheartradio.techtalk.controller.UserController
import org.iheartradio.techtalk.domain.DB
import spark.Route
import spark.Spark.*

const val DEFAULT_PORT = 4567
const val KEY_POST_ENV_VAR = "PORT"
const val USER_PATH = "/user"
const val POST_PATH = "/post"


fun main(args: Array<String>) {

    port(herokuPort)

    DB.init()

    get("/") { req, res ->
        res.status(HttpStatus.OK_200)
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
        post(PostController.insertInto)
    }

}

private val herokuPort = ProcessBuilder()
    .let { it.environment()[KEY_POST_ENV_VAR]}
    .takeIf { port -> port != null }
    ?.let { Integer.parseInt(it) }
    ?: DEFAULT_PORT

fun get(route: Route) {
    get("", route)
}

fun delete(route: Route) {
    delete("", route)
}

fun post(route: Route) {
    post("", route)
}

fun patch(route: Route) {
    patch("", route)
}