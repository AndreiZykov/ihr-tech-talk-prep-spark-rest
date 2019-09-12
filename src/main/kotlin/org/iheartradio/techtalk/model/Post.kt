package org.iheartradio.techtalk.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.iheartradio.techtalk.service.DistanceCalculator
import org.joda.time.DateTime

enum class LikeDislikeStatus {
    Liked,
    Disliked,
    Neutral
}

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
    val authorizedUserExtras: PostExtras? = null,
    val latLng: LatLng,
    val distanceFromAuthorizedUser: Float? = null
) : EntityModel {
    companion object {
        infix fun from(jsonString: String): Post = Gson().fromJson(jsonString, Post::class.java)
    }
}


class PostComparator: Comparator<Post> {
    override fun compare(p1: Post, p2: Post): Int {
        val dist = DistanceCalculator.calculateDistance(p1.latLng, p2.latLng)
        return when {
            dist > 0 -> 1
            dist < 0 -> -1
            else -> 0
        }
    }
}