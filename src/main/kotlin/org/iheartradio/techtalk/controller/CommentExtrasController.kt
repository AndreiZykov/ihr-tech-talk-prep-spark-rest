package org.iheartradio.techtalk.controller

import org.iheartradio.techtalk.model.response.ResponseList
import org.iheartradio.techtalk.service.CommentExtrasService
import spark.Route

object CommentExtrasController {



//    val update = Route { request, _ ->
//        CommentService.update(request.commentModel())
//        BaseResponse()
//    }


    val selectAll = Route { _, _ ->
        val users = CommentExtrasService.all()
        ResponseList(users)
    }

}