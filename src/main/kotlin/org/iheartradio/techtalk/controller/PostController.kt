package org.iheartradio.techtalk.controller

import org.iheartradio.techtalk.domain.dao.PostDao
import org.iheartradio.techtalk.domain.dao.UserDao
import org.iheartradio.techtalk.domain.dao.toPost
import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.model.FetchByLocation
import org.iheartradio.techtalk.model.LatLng
import org.iheartradio.techtalk.model.Post
import org.iheartradio.techtalk.model.response.BaseResponse
import org.iheartradio.techtalk.model.response.ResponseList
import org.iheartradio.techtalk.model.response.ResponseObject
import org.iheartradio.techtalk.model.response.SuccessResponse
import org.iheartradio.techtalk.service.PostService
import org.iheartradio.techtalk.sparkutils.auth
import org.iheartradio.techtalk.sparkutils.fetchByLocationBody
import org.iheartradio.techtalk.sparkutils.postModel
import org.iheartradio.techtalk.utils.APIException
import org.iheartradio.techtalk.utils.ErrorType
import org.iheartradio.techtalk.utils.extensions.toJson
import org.iheartradio.techtalk.utils.toBaseResponse
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Route


object PostController {

    val selectById = Route { request, _ ->
        val authorizedUserId = request.auth().authorizedUserId!!
        val postId = request.params("id").toLong()
        //TODO: Removing paging from this call. We will be fetching the replies with another endpoint/call
        val page: Int = request.queryMap("page").integerValue() ?: 1
        val post = transaction {
            PostDao.findById(postId)?.toPost(authorizedUserId)
        }
        post?.toJson()
    }

//    val insertInto = Route { request, _ ->
//        val post = request.postModel()
//        runCatching { PostService.new(post) }
//            .fold(
//                { newPost -> ResponseObject(newPost) },
//                { e ->
//                    return@Route (e as? APIException)?.toBaseResponse() ?: BaseResponse.of(ErrorType.SERVER_ERROR)
//                })
//            .takeIf { request.auth().authorizedUserId == post.userId }
//            ?: BaseResponse.of(ErrorType.FORBIDDEN)
//    }


    val insertInto = Route { request, _ ->
        val post = request.postModel()
        ResponseObject(PostService.new(post))
            .takeIf { request.auth().authorizedUserId == post.userId }
            ?: BaseResponse.of(ErrorType.FORBIDDEN)
    }


    val insertReply = Route { request, _ ->
        val reply = request.postModel()
        val replyToPostId = request.params("id").toLong()
        ResponseObject(PostService.reply(replyToPostId, reply))
            .takeIf { request.auth().authorizedUserId == reply.userId }
            ?: BaseResponse.of(ErrorType.FORBIDDEN)
    }

//    val repost = Route { request, _ ->
//        val newPost = request.postModel()
//        val authorizedUserId = request.auth().authorizedUserId
//        val originalPostId = request.params("id").toLong()
//        ResponseObject(PostService.repost(newPost, originalPostId))
//            .takeIf { authorizedUserId == newPost.userId }
//            ?: BaseResponse.of(ErrorType.FORBIDDEN)
//    }


    val repost = Route { request, _ ->
        val authorizedUserId = request.auth().authorizedUserId ?: 0
        val originalPostId = request.params("id").toLong()
        ResponseObject(PostService.repost(authorizedUserId, originalPostId))
    }

    val quote = Route { request, _ ->
        val newPost = request.postModel()
        val authorizedUserId = request.auth().authorizedUserId
        val quotedPostId = request.params("id").toLong()
        ResponseObject(PostService.quote(newPost, quotedPostId))
            .takeIf { authorizedUserId == newPost.userId }
            ?: BaseResponse.of(ErrorType.FORBIDDEN)
    }

    val dislike = Route { request, _ ->
        val authorizedUserId = request.auth().authorizedUserId ?: 0
        val postId = request.params("id").toLong()
//        PostService.dislike(authorizedUserId, postId)
//        SuccessResponse()
        ResponseObject(PostService.dislike(authorizedUserId, postId))
    }

    val like = Route { request, _ ->
        val authorizedUserId = request.auth().authorizedUserId ?: 0
        val postId = request.params("id").toLong()
        ResponseObject(PostService.like(authorizedUserId, postId))
    }

    val feed = Route { request, _ ->
        return@Route try {
            val authorizedUserId = request.auth().authorizedUserId ?: 0
//            val locationFetchBody = request.fetchByLocationBody()
            val userLatitude = request.queryMap("latitude").doubleValue()
            val userLongitude = request.queryMap("longitude").doubleValue()
            val radius = request.queryMap("radius").doubleValue()
            val fetchByLocation = FetchByLocation(userLatitude, userLongitude, radius)
            println("DEBUG:: FETCHING FEED FOR USER $authorizedUserId, fetchByLocationParams= $fetchByLocation")
            val page: Int = request.queryMap("page").integerValue() ?: 1
            ResponseList(
                PostService.fetchFeed(
                    localUserId = authorizedUserId,
                    fetchByLocationParams = fetchByLocation,
                    page = page
                )
            )
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }


    val batchInsertPostTestData = Route { request, _ ->
        return@Route try {
            PostService.batchInsertTestData()
            SuccessResponse()
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }


    val replies = Route { request, response ->
        return@Route try {
            val authorizedUserId = request.auth().authorizedUserId ?: 0
            val postId = request.params("id").toLong()
            println("DEBUG:: FETCHING REPLIES FOR postId $postId")
            val page: Int = request.queryMap("page").integerValue() ?: 1
            ResponseList(
                PostService.fetchReplies(
                    localUserId = authorizedUserId,
                    postId = postId,
                    page = page
                )
            )
        } catch (exception: APIException) {
            exception.toBaseResponse()
        }
    }


//    val insertRepost = Route { request, _ ->
//        val originalPostId = request.params("id").toLong()
//        val post = request.postModel().apply {
//            originalPost = PostDao.findById(originalPostId)?.toPost()
//        }
//        runCatching { PostService.new(post) }
//            .fold(
//                { newPost -> ResponseObject(newPost) },
//                { e ->
//                    return@Route (e as? APIException)?.toBaseResponse() ?: BaseResponse.of(ErrorType.SERVER_ERROR)
//                })
//            .takeIf { request.auth().authorizedUserId == post.userId }
//            ?: BaseResponse.of(ErrorType.FORBIDDEN)
//    }


}