package org.iheartradio.techtalk.model.response

import com.google.gson.GsonBuilder
import org.iheartradio.techtalk.model.Post

class PostsResponse(val posts: List<Post>) : BaseResponse() {
    override fun toString(): String =
        GsonBuilder().setPrettyPrinting().create().toJson(this)
}