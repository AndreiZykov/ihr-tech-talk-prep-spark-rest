package org.iheartradio.techtalk.controller

import org.iheartradio.techtalk.model.response.BaseResponse
import org.iheartradio.techtalk.model.response.PostsResponse
import org.iheartradio.techtalk.model.response.UserResponse
import org.iheartradio.techtalk.model.response.UsersResponse
import org.iheartradio.techtalk.service.PostService
import org.iheartradio.techtalk.service.UserService
import org.iheartradio.techtalk.sparkutils.auth
import org.iheartradio.techtalk.sparkutils.userModel
import org.iheartradio.techtalk.utils.APIException
import org.iheartradio.techtalk.utils.ErrorType.FORBIDDEN
import org.iheartradio.techtalk.utils.toBaseResponse
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.SizedIterable
import spark.Route


object UserController {

    val selectAll = Route { _, _ ->
        val users = UserService.all()
        UsersResponse(users)
    }

    val insertInto = Route { request, _ ->
        try {
            val user = UserService.new(request.userModel())
            UserResponse(user)
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }

    val update = Route { request, _ ->
        // user can only update his own user data
        val authResult = request.auth()
        val user = request.userModel()
        if (user.id == authResult.authorizedUserId) {
            UserResponse(UserService.update(user))
        } else {
            BaseResponse.of(FORBIDDEN)
        }
    }

    val delete = Route { request, _ ->
        UserService.delete(request.userModel())
        BaseResponse()
    }

    val deleteAll = Route { _, _ ->
        UserService.deleteAll()
        BaseResponse()
    }

    val signIn = Route { request, _ ->
        try {
            val user = UserService.signIn(request.userModel())
            UserResponse(user)
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }

    val selectPostsByUser = Route { request, _ ->
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
