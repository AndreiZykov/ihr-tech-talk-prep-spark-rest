package org.iheartradio.techtalk.model

import com.google.gson.Gson

data class User constructor(
    val id: Long = 0,
    val username: String = "",
    val password: String? = null,
    val jwt: String? = null
): EntityModel {


    companion object {
        infix fun from(jsonString: String): User = Gson().fromJson(jsonString, User::class.java)
    }
}