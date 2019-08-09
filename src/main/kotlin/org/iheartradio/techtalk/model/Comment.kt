package org.iheartradio.techtalk.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.joda.time.DateTime

data class Comment(
    val id: Long = 0,
    val userId: Long,
    val postId: Long,
    val body: String,
    val date: Long = DateTime.now().millis,
    val likeRating: Int = 0,
    val repostCount: Int = 0,
    val shareCount: Int = 0
) : EntityModel {
    companion object {
        infix fun from(jsonString: String): Comment = Gson().fromJson(jsonString, Comment::class.java)
    }
}