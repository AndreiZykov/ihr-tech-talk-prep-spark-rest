package org.iheartradio.techtalk.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.joda.time.DateTime

data class Post(val id: Long = 0,
                val userId: Long,
                val body: String,
                val date: Long,
                val likesCount: Int,
                val commentsCount: Int) {
    companion object {
        infix fun from(jsonString: String): Post = Gson().fromJson(jsonString, Post::class.java)
    }
}

fun Post.toJson(): String = GsonBuilder().setPrettyPrinting().create().toJson(this)
fun List<Post>.toJson(): String = Gson().toJson(this)
