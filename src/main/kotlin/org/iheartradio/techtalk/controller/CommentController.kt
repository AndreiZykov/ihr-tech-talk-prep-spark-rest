package org.iheartradio.techtalk.controller

import org.iheartradio.techtalk.model.response.BaseResponse
import org.iheartradio.techtalk.service.CommentService
import org.iheartradio.techtalk.sparkutils.commentModel
import spark.Route

object CommentController {

    val delete = Route { request, _ ->
        CommentService.delete(request.commentModel())
        BaseResponse()
    }

    val update = Route { request, _ ->
        CommentService.update(request.commentModel())
        BaseResponse()
    }


//    val insertInto = Route { request, response ->
//
//        val comment = Comment from request.body()
//
//        val newComment = transaction {
//            //INSERT new comment record
//            val commentDao = CommentDao.new {
//                userId = comment.userId
//                postId = comment.postId
//                body = comment.body
//                date = DateTime()
//                likesCount = 0
//                dislikesCount = 0
//            }
//
//            //Get total count of comments for this post
//            val totalComments = CommentDao.all()
//                .filter { it.postId == comment.postId }
//                .size
//
//            //UPDATE Post table record commentsCount to equal totalComments
//            PostDao.findById(comment.postId)
//                ?.apply { commentsCount = totalComments }
//
////            PostsTable.update ({
////                PostsTable.id eq request.params("userId").toLong()
////            }) {
////                with(SqlExpressionBuilder) {
////                    it.update(PostsTable.commentsCount, totalComments)
////                }
////            }
//
//            //Map CommentDao to Comment data class and then set response status to OK_200 if entire transaction succeeds
//            commentDao.toComment().also {
//                response.status(HttpStatus.OK_200)
//            }
//        }
//
//        return@Route newComment.toJson()
//    }
}