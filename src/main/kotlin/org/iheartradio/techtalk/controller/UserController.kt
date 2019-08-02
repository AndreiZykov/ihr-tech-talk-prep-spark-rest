package org.iheartradio.techtalk.controller

import org.eclipse.jetty.http.HttpStatus
import org.iheartradio.techtalk.domain.dao.PostDao
import org.iheartradio.techtalk.domain.dao.toPost
import org.iheartradio.techtalk.model.response.BaseResponse
import org.iheartradio.techtalk.model.response.PostsResponse
import org.iheartradio.techtalk.model.response.UserResponse
import org.iheartradio.techtalk.model.response.UsersResponse
import org.iheartradio.techtalk.model.toJson
import org.iheartradio.techtalk.service.PostService
import org.iheartradio.techtalk.service.UserService
import org.iheartradio.techtalk.sparkutils.auth
import org.iheartradio.techtalk.sparkutils.userModel
import org.iheartradio.techtalk.utils.APIException
import org.iheartradio.techtalk.utils.ErrorType
import org.iheartradio.techtalk.utils.ErrorType.*
import org.iheartradio.techtalk.utils.toBaseResponse
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Route


object UserController {

    val selectAll = Route { _, response ->
        val users = UserService.all()
        UsersResponse(users)
    }

    val insertInto = Route { request, response ->
        try {
            val user = UserService.new(request.userModel())
            UserResponse(user)
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }

    val update = Route { request, response ->
        // user can only update his own user data
        val authResult = request.auth()
        val user = request.userModel()
        if (user.id == authResult.authorizedUserId) {
            UserResponse(UserService.update(user))
        } else {
            BaseResponse.of(FORBIDDEN)
        }
    }

    val delete = Route { request, response ->
        UserService.delete(request.userModel())
        BaseResponse()
    }

    val deleteAll = Route { _, response ->
        UserService.deleteAll()
        BaseResponse()
    }

    val signIn = Route { request, response ->
        try {
            val user = UserService.signIn(request.userModel())
            UserResponse(user)
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }

    val selectPostsByUser = Route { request, response ->
        val userId: Long = request.params(":id").toLong()
        try {
            PostsResponse(PostService.allByUserId(userId))
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }
}

fun SizedIterable<Entity<*>>.deleteAll() {
    forEach { it.delete() }
}
