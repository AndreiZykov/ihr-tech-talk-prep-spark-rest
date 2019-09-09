package org.iheartradio.techtalk.model

import com.google.gson.Gson

data class FetchByLocation(
    val latitude: Double,
    val longitude: Double,
    val radius: Double
) {

    companion object {
        infix fun from(jsonString: String): FetchByLocation = Gson().fromJson(jsonString, FetchByLocation::class.java)
    }
}