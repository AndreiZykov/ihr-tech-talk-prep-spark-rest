package org.iheartradio.techtalk.model.response

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import org.iheartradio.techtalk.model.EntityModel


class SuccessResponse(val success: Boolean = true) : BaseResponse() {
    override fun toString(): String = GsonBuilder().setPrettyPrinting().create().toJson(this)
}

class ResponseObject<out T: EntityModel>(@SerializedName("response_object") val model: T?)
    : BaseResponse() {
    override fun toString(): String = GsonBuilder().setPrettyPrinting().create().toJson(this)
}

class ResponseList<out T: EntityModel>(@SerializedName("response_list") val list: List<T>)
    : BaseResponse() {
    override fun toString(): String = GsonBuilder().setPrettyPrinting().create().toJson(this)
}