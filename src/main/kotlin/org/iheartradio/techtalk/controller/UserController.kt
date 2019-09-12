package org.iheartradio.techtalk.controller

import org.iheartradio.techtalk.domain.entity.Users
import org.iheartradio.techtalk.domain.withMongoDbSession
import org.iheartradio.techtalk.model.response.*
import org.iheartradio.techtalk.repo.user.UserRepository
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
        ResponseList(users)
    }

    val selectById = Route { request, _ ->
        val localUserId = request.params("id").toLong()
        try {


            /*
       this.db.getCollection("users").findOne()?.let {
           println("(1st) DB OBJECT = $it")
       } ?: println("DB OBJECT FIND ONE (1st) == null")

       val params = DocumentSchemaQueryParams(Users, Users.idLong.equal(1))
       val u1 = this.find(params).iterator().asSequence().singleOrNull()
       println("u1 = $u1")
*/

            val user = UserRepository.fetch(localUserId)
            ResponseObject(user)
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }

    val insertInto = Route { request, _ ->
        try {
            val userModel = request.userModel()
            val user = UserService.new(userModel).copy(password = userModel.password)
            val id = withMongoDbSession{ Users.insert(user) }
            ResponseObject(UserService.signIn(user.copy(id= id, password = user.password)))
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }

    val update = Route { request, _ ->
        // user can only update his own user data
        val authResult = request.auth()
        val user = request.userModel()
        if (user.idLong == authResult.authorizedUserId) {
            ResponseObject(UserService.update(user))
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
            ResponseObject(user)
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }

    val selectPostsByUser = Route { request, _ ->
        val userId: Long = request.params(":id").toLong()
        try {
            ResponseList(PostService.allByUserId(userId))
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }
}

fun SizedIterable<Entity<*>>.deleteAll() {
    forEach { it.delete() }
}
