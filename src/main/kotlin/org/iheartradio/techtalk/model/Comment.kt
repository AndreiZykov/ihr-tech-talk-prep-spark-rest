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
    val likesCount: Int = 0,
    val dislikesCount: Int = 0
) {
    companion object {
        infix fun from(jsonString: String): Comment = Gson().fromJson(jsonString, Comment::class.java)
    }
}

fun Comment.toJson(): String = GsonBuilder().setPrettyPrinting().create().toJson(this)
fun List<Comment>.toJson(): String = Gson().toJson(this)