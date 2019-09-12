package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.model.LatLng
import org.iheartradio.techtalk.model.LikeDislikeStatus
import org.iheartradio.techtalk.model.Post
import org.iheartradio.techtalk.service.DistanceCalculator
import org.iheartradio.techtalk.service.PostExtrasService
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

class PostDao(id: EntityID<Long>) : LongEntity(id) {
    var user by UserDao referencedOn PostsTable.user
    var body by PostsTable.body
    var date by PostsTable.date
    var likesRating by PostsTable.likesRating
    var repostCount by PostsTable.repostCount
    var replyCount by PostsTable.replyCount
    var originalPostId by PostsTable.originalPostId
    var quotedPostId by PostsTable.quotedPostId
    var repliedPostId by PostsTable.repliedPostId
//    var geoLatitude by PostsTable.geoLatitude
//    var geoLongitude by PostsTable.geoLongitude


    var geoTag by PostsTable.geoTag
//    var geoLongitude by PostsTable.geoLongitude


//    var geoLatitude = lazy { geoTag.latitude }
//    var geoLongitude = lazy { geoTag.longitude }


//    var geoLatitude = lazy { geoTag.split(",").first().toDouble() }
//    var geoLongitude = lazy { geoTag.split(",").last().toDouble() }



    val geoLatitude get() = geoTag.split(",").last().toDouble()
    val geoLongitude get() = geoTag.split(",").last().toDouble()

    companion object : LongEntityClass<PostDao>(PostsTable)
}


fun PostDao.toPost(
    authorizedUserId: Long,
    userLocation: LatLng? = null
): Post {
    val originalPost: Post? = originalPostId?.let { PostDao.findById(it) }?.toPost(authorizedUserId)
    val quotedPost: Post? = quotedPostId?.let { PostDao.findById(it) }?.toPost(authorizedUserId)
    val authorizedUserExtras = PostExtrasService.find(authorizedUserId, id.value)
    val postLatLng = LatLng(
//        latitude = geoLatitude.toDouble(),
//        longitude = geoLongitude.toDouble()

//        latitude = geoTag.latitude,
//        longitude = geoTag.longitude

        latitude =  geoTag.split(",").first().toDouble(),
        longitude = geoTag.split(",").last().toDouble()
    )

//    val dist: Float = userLocation
//        ?.let { DistanceCalculator.calculateDistance(postLatLng, it) }
//        ?: Float.MAX_VALUE


    val dist: Float = 0.toFloat()

    return Post(
        id = id.value,
        userId = user.id.value,
        userName = user.username,
        body = body,
        date = date.millis,
        likesRating = likesRating,
        repostCount = repostCount,
        originalPost = originalPost,
        quotedPost = quotedPost,
        replyCount = replyCount,
        authorizedUserExtras = authorizedUserExtras,
        latLng = postLatLng,
        distanceFromAuthorizedUser = dist
    )
}
