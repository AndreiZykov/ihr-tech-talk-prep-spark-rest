package org.iheartradio.techtalk

import org.iheartradio.techtalk.controller.UserController
import org.iheartradio.techtalk.domain.DB
import spark.Spark.*

const val USER_PATH = "/user"

fun main() {

    DB.init()

    post("/signIn", UserController.signIn)

    path(USER_PATH) {
        get("/", UserController.fetchAllUsers)
        post("/", UserController.newUser)
        patch("/", UserController.updateUser)
        delete("/", UserController.deleteUser)
    }

}

