package org.iheartradio.techtalk.shared

import com.google.gson.Gson

data class User constructor(
    val id: Long = 0,
    val username: String = "",
    val password: String = "",
    val jwt: String
) {
    companion object {
        infix fun from(jsonString: String): User = Gson().fromJson(jsonString, User::class.java)
    }
}

fun User.toJson(): String = Gson().toJson(this)
fun List<User>.toJson(): String = Gson().toJson(this)