package org.iheartradio.techtalk.model

import com.google.gson.Gson
import kotlinx.nosql.Id
import org.iheartradio.techtalk.domain.entity.Users

data class User constructor(
    val id: Id<String, Users>? = null,
    val idLong: Long = 0,
    val username: String = "",
    val password: String? = null,
    val jwt: String? = null
): EntityModel {

//    val id: Id<String, Users>? = null

    companion object {
        infix fun from(jsonString: String): User = Gson().fromJson(jsonString, User::class.java)
    }
}