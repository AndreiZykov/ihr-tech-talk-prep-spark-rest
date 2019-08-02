package org.iheartradio.techtalk.utils

import org.iheartradio.techtalk.model.response.BaseResponse
import java.lang.Exception

class APIException(val error: ErrorType) : Exception()

fun APIException.toBaseResponse() = BaseResponse(errorMessage = error.message, errorCode = error.code)
