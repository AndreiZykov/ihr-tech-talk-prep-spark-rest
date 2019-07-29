package org.iheartradio.techtalk.controller

import org.eclipse.jetty.http.HttpStatus
import org.iheartradio.techtalk.domain.dao.CommentDao
import org.iheartradio.techtalk.domain.dao.PostDao
import org.iheartradio.techtalk.domain.dao.toComment
import org.iheartradio.techtalk.shared.Comment
import org.iheartradio.techtalk.shared.toJson
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import spark.Route

object CommentController {

    val newComment = Route { request, response ->

        val comment = Comment from request.body()

        val newComment = transaction {
            //INSERT new comment record
            val commentDao = CommentDao.new {
                userId = comment.userId
                postId = comment.postId
                body = comment.body
                date = DateTime()
                likesCount = 0
            }

            //Get total count of comments for this post
            val totalComments = CommentDao.all()
                .filter { it.postId == comment.postId }
                .size

            //UPDATE Post table record commentsCount to equal totalComments
            PostDao.findById(comment.postId)
                ?.apply { commentsCount = totalComments }

            //Map CommentDao to Comment data class and then set response status to OK_200 if entire transaction succeeds
            commentDao.toComment().also {
                response.status(HttpStatus.OK_200)
            }
        }

        return@Route newComment.toJson()
    }
}


/*
object CommentController {

    val newComment = Route { request, response ->
        val comment = Comment from request.body()

        var commentDao: CommentDao? = null

        val newUser = transaction {


            commentDao =  CommentDao.new {
                userId = comment.userId
                postId = comment.postId
                body = comment.body
                date = DateTime()
                likesCount = 0
            }


        }

        response.status(200)

        return@Route commentDao.toJson()
    }
}
 */