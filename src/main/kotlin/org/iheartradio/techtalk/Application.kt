package org.iheartradio.techtalk

import spark.kotlin.Http
import spark.kotlin.ignite

fun main(args: Array<String>) {
    val http: Http = ignite()
    http.get("/") {
        "Hello Spark Kotlin!"
    }
}