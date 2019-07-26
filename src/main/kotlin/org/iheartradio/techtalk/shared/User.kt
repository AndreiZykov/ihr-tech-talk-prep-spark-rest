package org.iheartradio.techtalk.shared

import com.google.gson.Gson

data class User constructor(val id: Int = 0, val username: String = "", val password: String = "", val jwt: String) {
    companion object {
        infix fun from(jsonString: String) =
            Gson().fromJson<User>(jsonString, User::class.java)!!
    }
}