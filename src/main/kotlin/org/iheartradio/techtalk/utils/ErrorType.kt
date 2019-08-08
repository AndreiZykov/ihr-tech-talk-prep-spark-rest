package org.iheartradio.techtalk.utils

import org.eclipse.jetty.http.HttpStatus

// internal error codes
enum class ErrorType(val code: Int, val message: String) {
    AUTHORIZATION_FAILED(401, "authorization failed"),
    FORBIDDEN(403, "action forbidden"),
    SERVER_ERROR(500, "internal server error"),
    USERNAME_EXIST(800, "username is already taken"),
    USER_NOT_FOUND(801, "user not found"),
    INVALID_PASSWORD(802, "invalid password"),
}