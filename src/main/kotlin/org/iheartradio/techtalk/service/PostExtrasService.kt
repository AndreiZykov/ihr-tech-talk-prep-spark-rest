package org.iheartradio.techtalk.service

import org.iheartradio.techtalk.domain.dao.*
import org.iheartradio.techtalk.domain.entity.PostExtrasTable
import org.iheartradio.techtalk.model.LikeDislikeStatus
import org.iheartradio.techtalk.model.PostExtras
import org.iheartradio.techtalk.utils.ErrorType
import org.iheartradio.techtalk.utils.apiException
import org.iheartradio.techtalk.utils.extensions.isNotEmpty
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object PostExtrasService {

    fun new(extras: PostExtras): PostExtras = transaction {
        UserDao.findById(extras.userId) ?: apiException(ErrorType.USER_NOT_FOUND)
        PostDao.findById(extras.postId) ?: apiException(ErrorType.POST_NOT_FOUND)
        PostExtrasDao.new {
            userId = extras.userId
            postId = extras.postId
            rating = extras.rating
            repost = extras.repost
        }.toPostExtra()
    }


    fun all(): List<PostExtras> = transaction { PostExtrasDao.all().map { it.toPostExtra() } }


    fun find(userId: Long, postId: Long): PostExtras? = transaction {
        PostExtrasDao.find { PostExtrasTable.userId.eq(userId) and PostExtrasTable.postId.eq(postId) }
            .firstOrNull()
            ?.toPostExtra()
    }

    fun doExtrasExist(
        userId: Long,
        postId: Long
    ): Boolean = transaction {
        PostExtrasDao.find { PostExtrasTable.userId.eq(userId) and PostExtrasTable.postId.eq(postId) }
            .isNotEmpty()
    }

    fun likeDislikeStatusForUser(
        userId: Long,
        postId: Long
    ): LikeDislikeStatus {
        val post = find(userId, postId) ?: return LikeDislikeStatus.Neutral
        return when {
            post.rating > 0 -> LikeDislikeStatus.Liked
            post.rating < 0 -> LikeDislikeStatus.Disliked
            else -> LikeDislikeStatus.Neutral
        }
    }
}