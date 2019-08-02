package org.iheartradio.techtalk.model.response

import com.google.gson.GsonBuilder
import org.iheartradio.techtalk.model.User

class UsersResponse(val users: List<User>) : BaseResponse() {
    override fun toString(): String =
        GsonBuilder().setPrettyPrinting().create().toJson(this)
}