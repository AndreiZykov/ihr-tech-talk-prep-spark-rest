package org.iheartradio.techtalk.controller

import org.iheartradio.techtalk.domain.dao.PostDao
import org.iheartradio.techtalk.domain.dao.toPost
import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.shared.toJson
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Route

object PostController {

    val fetchPostsForUser = Route { request, response ->
        val posts = transaction {
            PostDao.find {
                PostsTable.userId eq request.params("userId").toLong()
            }.map { it.toPost() }
        }

        response.status(200)
        return@Route posts.toJson()
    }

}