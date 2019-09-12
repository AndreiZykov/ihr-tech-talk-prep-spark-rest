package org.iheartradio.techtalk.domain.entity

import kotlinx.nosql.*
import kotlinx.nosql.mongodb.DocumentSchema
import org.iheartradio.techtalk.domain.entity.PostsTable.default
import org.iheartradio.techtalk.domain.entity.PostsTable.index
import org.iheartradio.techtalk.domain.entity.PostsTable.nullable
import org.iheartradio.techtalk.domain.entity.UsersTable.nullable
import org.iheartradio.techtalk.model.LatLng
import org.iheartradio.techtalk.model.Post
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.joda.time.DateTime
import java.math.BigDecimal
import java.math.RoundingMode
//import org.jetbrains.kotlin
const val POST_MAX_CHARS = 500


object Posts: DocumentSchema<Post>("posts", Post::class) {
    val user = id("USER", Users)
    val body = string(name = "BODY")
    val date = dateTime(name = "DATE")
    val likesRating = integer("LIKES_RATING")
    val repostCount = integer("REPOST_COUNT")
    val replyCount = integer("REPLY_COUNT")
    //the original post that this re-posted
    val originalPostId = nullableLong("ORIGINAL_POST_ID")
    //the post that this post was quoted from
    val quotedPostId = nullableLong("QUOTED_POST_ID")
    //the post id that this post is reply to, temp solution
    val repliedPostId = nullableLong("REPLIED_POST_ID")
    val geoLatitude = float(name = "GEO_LATITUDE")
    val geoLongitude = float(name = "GEO_LONGITUDE")
}

object PostsTable : LongIdTable() {
    val user = reference("USER", UsersTable)
    val body = varchar(name = "BODY", length = POST_MAX_CHARS)
    val date = datetime(name = "DATE").default(DateTime())
    val likesRating = integer("LIKES_RATING").default(0)
    val repostCount = integer("REPOST_COUNT").default(0)
    val replyCount = integer("REPLY_COUNT").default(0)
    //the original post that this re-posted
    val originalPostId = long("ORIGINAL_POST_ID").nullable()
    //the post that this post was quoted from
    val quotedPostId = long("QUOTED_POST_ID").nullable()
    //the post id that this post is reply to, temp solution
    val repliedPostId = long("REPLIED_POST_ID").nullable()






    val geoLatitude = decimal(name = "GEO_LATITUDE",
        precision = 20,
        scale = 15)
        .default(defaultValue = BigDecimal.ZERO)
        .index("post_latitude")

    val geoLongitude = decimal(name = "GEO_LONGITUDE",
        precision = 20,
        scale = 15)
        .default(defaultValue = BigDecimal.ZERO)
        .index("post_longitude")


    val geoTag = varchar(name = "LOCATION", length = 250)

//    val geoTag = registerColumn<LatLng>("LOCATION", PointColumnType())
//
////        .default(defaultValue = LatLng(0.toDouble(),0.toDouble()))
//        .index("post_geo_tag")

    /*


    val geoTag = point("LOCATION")
        .index("post_geo_tag")
     */

}



//
//class PointColumnType(val lat: Float, val long: Float): ColumnType() {
//    override fun sqlType(): String  = "POINT($lat, $long)"
//    override fun valueFromDB(value: Any): Any {
//        val valueFromDB = super.valueFromDB(value)
//        println("valueFromDB = $valueFromDB")
//        println("valueFromDB class = ${valueFromDB::class.java.simpleName}")
//        return when (valueFromDB) {
//            is String -> ""
//            else -> valueFromDB
//        }
//    }
//}
//
//
//fun Table.point(name: String, lat: Float, long: Float): Column<BigDecimal> = registerColumn(name, PointColumnType(lat, long))




class PointColumnType : ColumnType() {
    override fun sqlType(): String  = "POINT(0,0)"
    override fun valueFromDB(value: Any): Any {
        val valueFromDB = super.valueFromDB(value)
        println("valueFromDB = $valueFromDB")
        println("valueFromDB class = ${valueFromDB::class.java.simpleName}")
        return when (valueFromDB) {
            is String -> ""
            else -> valueFromDB
        }
    }
}


fun Table.point(name: String): Column<String> = registerColumn(name, PointColumnType())