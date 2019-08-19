package org.iheartradio.techtalk.model

import com.google.gson.Gson

data class PostExtras(
    val id: Long = 0,
    val userId: Long,
    val postId: Long,
    val like: Int = 0,
    val dislike: Int = 0,
    val repost: Int = 0
) : EntityModel {
    companion object {
        infix fun from(jsonString: String): PostExtras = Gson().fromJson(jsonString, PostExtras::class.java)
    }
}