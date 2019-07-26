package org.iheartradio.techtalk.ext

import com.google.gson.Gson
import org.iheartradio.techtalk.domain.dao.UserDao
import org.iheartradio.techtalk.shared.User

fun UserDao.toUser() = User(id = id.value, username = username, password = password_hash, jwt = jwt ?: "")
fun User.toJson() = Gson().toJson(this)!!
fun List<User>.toJson() = Gson().toJson(this)!!