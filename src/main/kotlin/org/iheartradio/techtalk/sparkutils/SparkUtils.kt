package org.iheartradio.techtalk.sparkutils

import org.eclipse.jetty.http.HttpHeader.AUTHORIZATION
import org.eclipse.jetty.http.HttpStatus
import org.iheartradio.techtalk.model.*
import org.iheartradio.techtalk.model.response.BaseResponse
import org.iheartradio.techtalk.service.UserService
import org.iheartradio.techtalk.utils.ErrorType.AUTHORIZATION_FAILED
import spark.Request
import spark.Route
import spark.Spark.*

//fun Request.auth(): UserService.AuthResult {
//    val jwt = headers(AUTHORIZATION.asString()) ?: ""
//    val authResult = UserService.auth(jwt)
//    if (!authResult.success) {
//        halt(HttpStatus.UNAUTHORIZED_401, BaseResponse.of(AUTHORIZATION_FAILED).toString())
//    }
//    return authResult
//}


fun Request.auth(): UserService.AuthResult {
    val jwt = headers(AUTHORIZATION.asString()) ?: ""
    return UserService.auth(jwt).also { authResult ->
        if (!authResult.success) {
            halt(HttpStatus.UNAUTHORIZED_401, BaseResponse.of(AUTHORIZATION_FAILED).toString())
        }
    }
}

// request ext functions
fun Request.userModel(): User = User from body()
fun Request.postModel(): Post = Post from body()
fun Request.commentModel(): Comment = Comment from body()
fun Request.commentExtrasModel(): PostExtras = PostExtras from body()
fun Request.latLngModel(): LatLng =  LatLng from body()


fun get(route: Route) { get("", route)  }
fun delete(route: Route) {  delete("", route) }
fun post(route: Route) {  post("", route)  }
fun patch(route: Route) {  patch("", route) }
