package org.iheartradio.techtalk.model.response

import com.google.gson.GsonBuilder
import org.iheartradio.techtalk.model.EntityModel

class ResponseObject<out T: EntityModel>(val model: T) : BaseResponse() {
    override fun toString(): String = GsonBuilder().setPrettyPrinting().create().toJson(this)
}

class ResponseList<out T: EntityModel>(val list: List<T>) : BaseResponse() {
    override fun toString(): String = GsonBuilder().setPrettyPrinting().create().toJson(this)
}