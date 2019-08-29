package org.iheartradio.techtalk.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.joda.time.DateTime

data class Post(
    val id: Long = 0,
    val userId: Long,
    val userName: String,
    val body: String,
    val date: Long = DateTime.now().millis,
    val likesRating: Int = 0,
    val repostCount: Int = 0,
    //post this was re-posted from
    var originalPost: Post? = null,
    var quotedPost: Post? = null,
    val replyCount: Int,
    val isLikedByMe: Boolean = false
) : EntityModel {
    companion object {
        infix fun from(jsonString: String): Post = Gson().fromJson(jsonString, Post::class.java)
    }
}