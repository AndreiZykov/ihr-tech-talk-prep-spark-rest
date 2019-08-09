package org.iheartradio.techtalk.utils

import org.eclipse.jetty.http.HttpStatus

// internal error codes
enum class ErrorType(val code: Int, val message: String) {
    AUTHORIZATION_FAILED(HttpStatus.UNAUTHORIZED_401, "authorization failed"),
    FORBIDDEN(HttpStatus.FORBIDDEN_403, "action forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND_404, "not found"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR_500, "internal server error"),
    USERNAME_EXIST(800, "username is already taken"),
    USER_NOT_FOUND(801, "user not found"),
    INVALID_PASSWORD(802, "invalid password"),
    INVALID_TOKEN(802, "invalid access token"),
    POST_NOT_FOUND(900, "post not found"),
    COMMENT_NOT_FOUND(901, "comment not found")
}