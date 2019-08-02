package org.iheartradio.techtalk.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.joda.time.DateTime

data class Post(
    val id: Long = 0,
    val userId: Long,
    val body: String,
    val date: Long = DateTime.now().millis,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val comments: List<Comment> = emptyList()
) : EntityModel {
    companion object {
        infix fun from(jsonString: String): Post = Gson().fromJson(jsonString, Post::class.java)
    }
}