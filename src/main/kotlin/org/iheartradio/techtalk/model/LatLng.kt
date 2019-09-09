package org.iheartradio.techtalk.model

import com.google.gson.Gson

data class LatLng(val latitude: Double,
                  val longitude: Double) {

    companion object {
        infix fun from(jsonString: String): LatLng = Gson().fromJson(jsonString, LatLng::class.java)
    }
}