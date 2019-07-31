package org.iheartradio.techtalk.shared

import com.google.gson.Gson
import org.joda.time.DateTime

data class Comment(
    val id: Long = 0,
    val userId: Long,
    val postId: Long,
    val body: String,
    val date: DateTime = DateTime(),
    val likesCount: Int = 0
) {
    companion object {
        infix fun from(jsonString: String): Comment = Gson().fromJson(jsonString, Comment::class.java)
    }
}

fun Comment.toJson(): String = Gson().toJson(this)
fun List<Comment>.toJson(): String = Gson().toJson(this)