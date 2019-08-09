package org.iheartradio.techtalk.model

import com.google.gson.Gson
import org.iheartradio.techtalk.domain.entity.CommentExtrasTable

data class CommentExtras(
    val id: Long = 0,
    val userId: Long,
    val commentId: Long,
    val like: Int = 0,
    val dislike: Int = 0,
    val repost: Int = 0,
    val share: Int = 0
) : EntityModel {
    companion object {
        infix fun from(jsonString: String): CommentExtras = Gson().fromJson(jsonString, CommentExtras::class.java)
    }
}