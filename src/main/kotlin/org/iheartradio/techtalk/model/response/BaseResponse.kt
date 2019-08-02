package org.iheartradio.techtalk.model.response

import com.google.gson.GsonBuilder
import org.iheartradio.techtalk.utils.ErrorType

open class BaseResponse (val errorMessage: String? = null, val errorCode: Int? = null){
    override fun toString(): String =
        GsonBuilder().setPrettyPrinting().create().toJson(this)

    companion object {
        fun of(errorType: ErrorType) = BaseResponse(errorType.message, errorType.code)
    }
}
