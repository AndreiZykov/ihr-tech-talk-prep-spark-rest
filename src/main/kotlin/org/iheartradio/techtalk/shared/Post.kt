package org.iheartradio.techtalk.shared

import com.google.gson.Gson
import org.iheartradio.techtalk.domain.dao.PostDao
import org.joda.time.DateTime
import java.util.*

data class Post(val id: Long = 0,
                val userId: Long,
                val body: String,
                val date: DateTime,
                val likesCount: Int,
                val commentsCount: Int) {
    companion object {
        infix fun from(jsonString: String): Post = Gson().fromJson(jsonString, Post::class.java)
    }
}

fun Post.toJson(): String = Gson().toJson(this)
fun List<Post>.toJson(): String = Gson().toJson(this)
