package org.iheartradio.techtalk.utils.extensions

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.iheartradio.techtalk.model.Comment
import org.iheartradio.techtalk.model.Post

fun Any.toJson(): String = GsonBuilder().setPrettyPrinting().create().toJson(this)
fun List<Any>.toJson(): String = Gson().toJson(this)