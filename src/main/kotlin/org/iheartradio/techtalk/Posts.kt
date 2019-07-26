package org.iheartradio.techtalk

import com.google.gson.Gson
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Posts : IntIdTable() {
    val userId = long(name = "USER_ID")
    val body = varchar(name = "BODY", length = Integer.MAX_VALUE)
    val likesCount = integer(name = "LIKES_COUNT")
    val commentsCount = integer(name = "COMMENTS_COUNT")
}

class Post(id: EntityID<Int>) : IntEntity(id) {
    var userId by Posts.userId
    var body by Posts.body
    var likesCount by Posts.likesCount
    var commentsCount by Posts.commentsCount

    companion object : IntEntityClass<User>(Users)

    infix fun update(model: PostModel) {
        userId = model.userId
        body = model.body
        likesCount = model.likesCount
        commentsCount = model.commentsCount
    }
}

data class PostModel constructor(
    val id: Int = 0,
    val userId: Long,
    val body: String,
    val likesCount: Int,
    val commentsCount: Int
) {
    companion object {
        infix fun from(jsonString: String): PostModel = Gson().fromJson(
            jsonString,
            PostModel::class.java
        )
    }
}

fun Post.toPostModel() = PostModel(
    id = id.value,
    userId = userId,
    body = body,
    likesCount = likesCount,
    commentsCount = commentsCount
)

fun PostModel.toJson(): String = Gson().toJson(this)
fun List<PostModel>.toJson(): String = Gson().toJson(this)