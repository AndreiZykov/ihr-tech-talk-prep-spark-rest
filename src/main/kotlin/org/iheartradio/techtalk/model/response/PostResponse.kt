package org.iheartradio.techtalk.model.response

import com.google.gson.GsonBuilder
import org.iheartradio.techtalk.model.Post

class PostResponse(val post: Post) : BaseResponse() {
    override fun toString(): String =
        GsonBuilder().setPrettyPrinting().create().toJson(this)
}