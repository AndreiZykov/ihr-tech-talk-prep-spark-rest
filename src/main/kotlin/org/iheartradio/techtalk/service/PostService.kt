package org.iheartradio.techtalk.service

import org.iheartradio.techtalk.SQLStatement
import org.iheartradio.techtalk.controller.deleteAll
import org.iheartradio.techtalk.domain.DbFunctions
import org.iheartradio.techtalk.domain.dao.*
import org.iheartradio.techtalk.domain.entity.PostExtrasTable
import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.domain.entity.RepliesTable
import org.iheartradio.techtalk.model.*
import org.iheartradio.techtalk.utils.APIException
import org.iheartradio.techtalk.utils.ErrorType
import org.iheartradio.techtalk.utils.apiException
import org.iheartradio.techtalk.utils.extensions.execAndMap
import org.iheartradio.techtalk.utils.extensions.paginate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.math.BigDecimal
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random


fun <T> T.toLazy() = lazy { this }

object PostService {

    fun new(post: Post): Post = transaction {
        val localUser = UserDao.findById(post.userId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
        PostDao.new {
            user = localUser
            body = post.body
            date = DateTime()
            likesRating = 0
            repostCount = 0
            originalPostId = post.originalPost?.id
//            geoLatitude = post.latLng.latitude.toBigDecimal()
//            geoLongitude = post.latLng.longitude.toBigDecimal()
//            geoLatitude = post.latLng.latitude.toLazy()
//            geoLongitude = post.latLng.longitude.toLazy()
            geoTag = "${post.latLng.latitude},${post.latLng.longitude}"
        }.toPost(localUser.id.value)
    }


//    fun repost(post: Post, originalPostId: Long): Post = transaction {
//        val localUser = UserDao.findById(post.userId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
//        val originalPost: Post = PostDao.findById(originalPostId)?.toPost() ?: apiException(ErrorType.POST_NOT_FOUND)
//        val newPost = PostDao.new {
//            user = localUser
//            body = post.body
//            date = DateTime()
//            likesRating = 0
//            repostCount = 0
//            this.originalPostId = originalPost.id
//        }.toPost()
//
//        val localUserId = localUser.id.value
//
//        val extras = PostExtrasService.find(localUserId, originalPost.id)
//
//        if (extras != null) {
//            PostExtrasDao.findById(extras.id)?.updateRepost()
//        } else {
//            PostExtrasService.new(
//                PostExtras(
//                    userId = localUserId,
//                    postId = originalPost.id,
//                    repost = 1
//                )
//            )
//        }
//
//        val totalReposts = PostExtrasDao
//            .find { PostExtrasTable.postId.eq(originalPost.id) and PostExtrasTable.repost.greater(0) }
//            .count()
//
//
//        PostDao.findById(originalPost.userId)?.apply {
//            repostCount = totalReposts
//        }
//
//        newPost
//    }


    fun repost(localUserId: Long, originalPostId: Long): Post? = transaction {
        val localUser = UserDao.findById(localUserId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
        val originalPost: Post =
            PostDao.findById(originalPostId)?.toPost(localUser.id.value) ?: apiException(ErrorType.POST_NOT_FOUND)
//        val alreadyReposted = PostDao.find {
//            PostsTable.originalPostId.eq(originalPostId) and PostsTable.user.eq(localUserId)
//        }.count() > 0


//        val newPost = PostDao.new {
//            user = localUser
//            body = originalPost.body
//            date = DateTime()
//            likesRating = 0
//            repostCount = 0
//            this.originalPostId = originalPost.id
//        }.toPost(localUser.id.value)

        var newPost: Post? = null

        val extras = PostExtrasService.find(localUserId, originalPost.id)

        if (extras != null) {
//            PostExtrasDao.findById(extras.id)?.updateRepost()

            val updatedExtras = PostExtrasDao.findById(extras.id)?.apply {
                repost = if (repost > 0) 0 else 1
            }

            if (updatedExtras?.repost == 1) {
                //insert new post with original post details
                newPost = PostDao.new {
                    user = localUser
                    body = originalPost.body
                    date = DateTime()
                    likesRating = 0
                    repostCount = 0
                    this.originalPostId = originalPost.id
                }.toPost(localUser.id.value)
            } else {
                //delete the re-posted post
                PostDao
                    .find { PostsTable.originalPostId.eq(originalPost.id) and PostsTable.user.eq(localUser.id) }
                    .deleteAll()
//                    .firstOrNull()
//                    ?.delete()


            }

        } else {
            PostExtrasService.new(
                PostExtras(
                    userId = localUserId,
                    postId = originalPost.id,
                    repost = 1
                )
            )
        }

        val totalReposts = PostExtrasDao
            .find { PostExtrasTable.postId.eq(originalPost.id) and PostExtrasTable.repost.greater(0) }
            .count()


        PostDao.findById(originalPost.id)?.apply {
            repostCount = totalReposts
        }

        newPost
    }


    fun quote(post: Post, quotedPostId: Long): Post = transaction {
        val localUser = UserDao.findById(post.userId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
        val quotedPost: Post =
            PostDao.findById(quotedPostId)?.toPost(localUser.id.value) ?: apiException(ErrorType.POST_NOT_FOUND)
        PostDao.new {
            user = localUser
            body = post.body
            date = DateTime()
            likesRating = 0
            repostCount = 0
            this.quotedPostId = quotedPost.id
        }.toPost(localUser.id.value)
    }

    fun dislike(userId: Long, postId: Long): Post = transaction {

        val post = PostDao.findById(postId) ?: apiException(ErrorType.POST_NOT_FOUND)

        val extras = PostExtrasService.find(userId, postId)

        if (extras != null) { //recordExists
            PostExtrasDao.findById(extras.id)?.updateDislike()
        } else {
            PostExtrasService.new(
                PostExtras(
                    userId = userId,
                    postId = postId,
                    rating = -1
                )
            )
        }

        val totalLikes = PostExtrasDao
//                .find { PostExtrasTable.userId.eq(userId) and PostExtrasTable.like.greater(0) }
            .find { PostExtrasTable.postId.eq(postId) and PostExtrasTable.like.greater(0) }
            .count()

        val totalDislike = PostExtrasDao
            .find { PostExtrasTable.postId.eq(postId) and PostExtrasTable.like.less(0) }
            .count()

        post.apply {
            likesRating = totalLikes - totalDislike

        }.toPost(userId)
    }

//    fun like(userId: Long, postId: Long) {
//        transaction {
//
//            val post = PostDao.findById(postId) ?: apiException(ErrorType.POST_NOT_FOUND)
//
//            val extras = PostExtrasService.find(userId, postId)
//
//            if (extras != null) { //recordExists
//                PostExtrasDao.findById(extras.id)?.updateLike()
//            } else {
//                PostExtrasService.new(
//                    PostExtras(
//                        userId = userId,
//                        postId = postId,
//                        like = 1
//                    )
//                )
//            }
//
//            val totalLikes = PostExtrasDao
//                .find { PostExtrasTable.postId.eq(postId) and PostExtrasTable.like.greater(0) }
//                .count()
//
//            val totalDislike = PostExtrasDao
//                .find { PostExtrasTable.postId.eq(postId) and PostExtrasTable.like.less(0) }
//                .count()
//
//            post.apply {
//                likesRating = totalLikes - totalDislike
//            }
//        }
//    }


    fun like(userId: Long, postId: Long): Post = transaction {

        val post = PostDao.findById(postId) ?: apiException(ErrorType.POST_NOT_FOUND)

        val extras = PostExtrasService.find(userId, postId)

        if (extras != null) { //recordExists
            PostExtrasDao.findById(extras.id)?.updateLike()
        } else {
            PostExtrasService.new(
                PostExtras(
                    userId = userId,
                    postId = postId,
                    rating = 1
                )
            )
        }

        val totalLikes = PostExtrasDao
            .find { PostExtrasTable.postId.eq(postId) and PostExtrasTable.like.greater(0) }
            .count()

        val totalDislike = PostExtrasDao
            .find { PostExtrasTable.postId.eq(postId) and PostExtrasTable.like.less(0) }
            .count()

        post.apply {
            likesRating = totalLikes - totalDislike
        }.toPost(userId)
    }

    fun reply(
        replyToPostId: Long,
        reply: Post
    ): Post = transaction {

        val localUser = UserDao.findById(reply.userId) ?: throw APIException(ErrorType.USER_NOT_FOUND)

        //Validate that main post (post we are replying to) exists
        val postReplyingTo = PostDao.findById(replyToPostId) ?: apiException(ErrorType.POST_NOT_FOUND)

        //Create Reply Post
        val newReply = PostDao.new {
            user = localUser
            body = reply.body
            date = DateTime()
            likesRating = 0
            repostCount = 0
            originalPostId = null
            repliedPostId = replyToPostId
        }

        //Insert new child relation record
        RepliesDao.new {
            this.postId = postReplyingTo.id.value
            this.replyPostId = newReply.id.value
        }

        //Get count of all replies for this main post (post we are replying to)
        val mainPostReplyCount = RepliesDao
            .find { RepliesTable.postId.eq(replyToPostId) }
            .count()

        //Update the main post's (post we are replying to) reply_count
        postReplyingTo.apply {
            replyCount = mainPostReplyCount
        }

        newReply.toPost(localUser.id.value)
    }

    fun allByUserId(localUserId: Long): List<Post> {
        return transaction {
            val localUser = UserDao.findById(localUserId) ?: throw APIException(ErrorType.USER_NOT_FOUND)
            localUser.posts.map { it.toPost(localUser.id.value) }
        }
    }


    fun fetchFeed(
        localUserId: Long,
        fetchByLocationParams: FetchByLocation,
        page: Int = 1,
        pageItemCount: Int = 10
    ) = transaction {
        println("DEBUG:: fetchFeed called for $localUserId")

//        PostDao.find{ PostsTable.repliedPostId.isNull()  }
//            .orderBy(PostsTable.date to SortOrder.DESC)
//            .paginate(page, pageItemCount)
//            .map { it.toPost(localUserId) }


        /*
        val localUserLocation = LatLng(fetchByLocationParams.latitude, fetchByLocationParams.longitude)

        val distanceOp = DistanceOp(
            PostsTable.geoLatitude,
            PostsTable.geoLongitude,
            localUserLocation.latitude,
            localUserLocation.longitude,
            PostsTable.geoLatitude.columnType
        )
        val radius = BigDecimal(fetchByLocationParams.radius)
//


//        //Return all posts that ARE NOT Replies and Distance from user LessThan
        PostDao.find { PostsTable.repliedPostId.isNull() and distanceOp.lessEq(radius) }
//        PostDao.find { PostsTable.repliedPostId.isNull()}
//            .orderPosts(distanceOp)
            .paginate(page, pageItemCount)
            .map { it.toPost(localUserId, localUserLocation) }


         */


//        SQLStatement("select p.id, p.user as user_id,\n" +
//                "p.geo_latitude, \n" +
//                "p.geo_longitude,\n" +
//                "calculate_distance(p.geo_latitude,p.geo_longitude,40.25177,-74.400009,'N') as dist\n" +
//                "from posts p\n" +
//                "order by dist asc\n" +
//                "limit 20;\n").execAndMap {
//            Post(id = it.getLong("id"),
//                userId = it.getLong("user_id"),
//                userName = "",
//                body = "",
//                replyCount = 0,
//                latLng = LatLng(it.getBigDecimal("geo_latitude").toDouble(), it.getBigDecimal("geo_longitude").toDouble()))
//        }


        val localUserLocation = LatLng(fetchByLocationParams.latitude, fetchByLocationParams.longitude)

        val radius = fetchByLocationParams.radius
        val latitude = localUserLocation.latitude
        val longitude = localUserLocation.longitude


        /*

        val earth = 6378.137  //radius of the earth in kilometer
        val pi = Math.PI
//        val m = (1 / ((2 * pi / 360) * earth)) / 1000;  //1 meter in degree
        val km = (1 / ((2 * pi / 360) * earth)) ;  //1 kilometer in degree
        val right = latitude + (radius * km) //going right
        val left = latitude - (radius * km) //going left

        val bottom = longitude + (radius * km) / cos(latitude * (pi / 180))
        val top = longitude - (radius * km) / cos(latitude * (pi / 180))

        */


//        val earth = 6371
////        val left = longitude + Math.toDegrees(radius / earth / cos(Math.toRadians(latitude)))
////        val right = longitude - Math.toDegrees(radius / earth / cos(Math.toRadians(latitude)))
////        val bottom = latitude + Math.toDegrees(radius / earth)
////        val top = latitude - Math.toDegrees(radius / earth)
//
//
//        val left = latitude - Math.toDegrees(radius / earth / cos(Math.toRadians(latitude)))
//        val right = latitude + Math.toDegrees(radius / earth / cos(Math.toRadians(latitude)))
//        val bottom = longitude + Math.toDegrees(radius / earth)
//        val top = longitude - Math.toDegrees(radius / earth)
//
//        /*
//        double R = 6371;  // earth radius in km
//
//double radius = 50; // km
//
//double x1 = lon - Math.toDegrees(radius/R/Math.cos(Math.toRadians(lat)));
//
//double x2 = lon + Math.toDegrees(radius/R/Math.cos(Math.toRadians(lat)));
//
//double y1 = lat + Math.toDegrees(radius/R);
//
//double y2 = lat - Math.toDegrees(radius/R);
//         */
//
//        val leftBounds: Op<Boolean> = PostsTable.geoLatitude greaterEq left.toBigDecimal()
//        val rightBounds: Op<Boolean> = PostsTable.geoLatitude lessEq right.toBigDecimal()
//        val topBounds: Op<Boolean> = PostsTable.geoLongitude greaterEq top.toBigDecimal()
//        val bottomBounds: Op<Boolean> = PostsTable.geoLongitude lessEq bottom.toBigDecimal()
////
//        println("left = $left")
//        println("right = $right")
//        println("top = $top")
//        println("bottom = $bottom")
//
//        val distanceOp = DistanceOp(
//            PostsTable.geoLatitude,
//            PostsTable.geoLongitude,
//            localUserLocation.latitude,
//            localUserLocation.longitude,
//            PostsTable.geoLatitude.columnType
//        )

//
//        var whereClause: Op<Boolean>
//
//        val whereClause1 =
//            PostsTable.repliedPostId.isNull() and leftBounds and rightBounds and topBounds and bottomBounds
//
//
//        val whereClause2 = PostsTable.repliedPostId.isNull() and distanceOp.lessEq(radius.toBigDecimal())
//
//
//        whereClause = whereClause1

//        println("WHERE CLAUSE: $whereClause")
//        PostDao.find { whereClause }
//            .orderBy(PostsTable.date to SortOrder.DESC)
//            .paginate(page, pageItemCount)
//            .map { it.toPost(localUserId, localUserLocation) }
//            .sortedWith(PostComparator())


        //SizedCollection(delegate.drop(offset).take(n))

//        PostDao.find { whereClause }
////            .orderBy(PostsTable.date to SortOrder.DESC)
////            .orderByDistance(localUserLocation)
////            .paginate(page, pageItemCount)
//            .map { it.toPost(localUserId, localUserLocation) }
//            .filter {
//                DistanceCalculator.calculateDistance(it.latLng, localUserLocation) <= radius
//            }
//            .take(5)


//        PostDao.all()
//            .map { it.toPost(localUserId, localUserLocation) }
//            .filter { DistanceCalculator.calculateDistance(it.latLng, localUserLocation) <= radius }



        /*
        "SELECT * FROM POSTS p\n" +
                "WHERE p.geo_latitude > (41.25177-2)\n" +
                "AND p.geo_latitude < (41.25177+2)\n" +
                "AND p.geo_longitude > (-74.400010-2)\n" +
                "AND p.geo_longitude < (-74.400010+2)\n" +
                "GROUP BY p.id, p.user, p.body, p.date, p.likes_rating, p.repost_count, p.reply_count,\n" +
                "p.original_post_id, p.quoted_post_id, p.replied_post_id, p.geo_latitude, p.geo_longitude,\n" +
                "p.location\n" +
                "HAVING calculate_distance(p.geo_latitude,p.geo_longitude,40.25177,-74.400009,'K') <= 200\n" +
                "LIMIT $n OFFSET $offset\n" +
                ";"
         */


        exec("DROP TABLE IF EXISTS temp_table;\n" +
                "CREATE TEMP TABLE IF NOT EXISTS temp_table AS\n" +
                "SELECT *, calculate_distance(p.geo_latitude,p.geo_longitude,40.25177,-74.400009,'K') as dist\n" +
                "FROM POSTS p\n" +
                "WHERE p.geo_latitude > (41.25177-2)\n" +
                "AND p.geo_latitude < (41.25177+2)\n" +
                "AND p.geo_longitude > (-74.400010-2)\n" +
                "AND p.geo_longitude < (-74.400010+2)\n" +
                ";")

        val n = pageItemCount
        val offset = ((page - 1) * pageItemCount)
        SQLStatement("SELECT * FROM temp_table p WHERE p.dist <= $radius\n" +
                "GROUP BY p.id, p.user, p.body, p.date, p.likes_rating, p.repost_count, p.reply_count,\n" +
                "p.original_post_id, p.quoted_post_id, p.replied_post_id, p.geo_latitude, p.geo_longitude,\n" +
                "p.location, p.dist\n" +
                "ORDER BY p.dist\n" +
                "LIMIT $n OFFSET $offset\n" +
                ";").execAndMap {
            val loc = it.getString("location")
            val latLng = LatLng(
                loc.split(",").first().toDouble(),
                loc.split(",").last().toDouble()
            )

//            val latLng = LatLng(0.0,0.0)
            Post(
                id = it.getLong("id"),
                userId = it.getLong("user"),
                userName = "",
                date = it.getDate("date").time,
                replyCount = 0,
                latLng = latLng,
                body = it.getString("body"),
                distanceFromAuthorizedUser = it.getFloat("dist")
            )
        }.toList()

//            .sortedWith(PostComparator())
    }


    fun fetchReplies(
        localUserId: Long,
        postId: Long,
        page: Int = 1,
        pageItemCount: Int = 10
    ) = transaction {
        println("DEBUG:: fetchFeed called for $localUserId")

        val replyIds = RepliesDao.find { RepliesTable.postId.eq(postId) }
            .map { it.replyPostId }
            .toList()

        PostDao.forIds(replyIds)
            .orderBy(PostsTable.date to SortOrder.DESC)
            .paginate(page, pageItemCount)
            .map { it.toPost(localUserId) }


    }


    fun batchInsertTestData() = transaction {

        val brooklyn = LatLng(40.6501, -73.94958)
        val toronto = LatLng(43.70011, -79.4163)
        val newark = LatLng(40.735657, -74.1723667)
        val cherryHill = LatLng(39.909939, -75.005991)
        val freehold = LatLng(40.260641, -74.275601)
        val greenwhich = LatLng(40.715149, -74.011151)
        val madison = LatLng(40.711569, -74.001752)

        val posts = mutableListOf<Post>()
        for (i in 1..100000) {

            val nextInt = Random.nextInt(1, 6)
            println("random location = $nextInt")
            val location = when (nextInt) {
                1 -> brooklyn
                2 -> toronto
                3 -> cherryHill
                4 -> freehold
                5 -> greenwhich
                6 -> newark
                else -> madison
            }

            posts.add(
                Post(
                    userId = 1,
                    userName = "Anthony",
                    body = "HELLO WORLD post #$i",
                    replyCount = 0,
                    latLng = location
                )
            )
        }

        PostsTable.batchInsert(posts) { post ->
            this[PostsTable.user] = UserDao.findById(post.userId)!!.id
            this[PostsTable.body] = post.body
            this[PostsTable.replyCount] = 0
//            this[PostsTable.geoLatitude] = post.latLng.latitude.toBigDecimal()
//            this[PostsTable.geoLongitude] = post.latLng.longitude.toBigDecimal()

            val latitude = post.latLng.latitude.toBigDecimal()
            val longitude = post.latLng.longitude.toBigDecimal()

            this[PostsTable.geoLatitude] = latitude
            this[PostsTable.geoLongitude] = longitude
            this[PostsTable.geoTag] = "$latitude,$longitude"

        }
    }
}


object DistanceCalculator {
    private val earthRadius = 6378.137.toFloat() //6371
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val dLat = Math.toRadians((lat2 - lat1))
        val dLon = Math.toRadians((lon2 - lon1))
        val a = (sin((dLat / 2)) * sin((dLat / 2)) + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin((dLon / 2)) * sin((dLon / 2)))).toFloat()
        val c = (2 * atan2(sqrt(a.toDouble()), sqrt((1 - a).toDouble()))).toFloat()
        return earthRadius * c
    }


    fun calculateDistance(loc1: LatLng, loc2: LatLng): Float {
        return calculateDistance(loc1.latitude, loc1.longitude, loc2.latitude, loc2.longitude)
    }
}


//fun com(lat: Double, lng: Double) {
//    return lat * 1e7 shl 16 and 0xffff0000 or (lng * 1e7) and 0x0000ffff
//}

//
//class MinusOp<T, S: T>(val expr1: Expression<T>,
//                       val expr2: Expression<S>,
//                       override val columnType: IColumnType): ExpressionWithColumnType<T>() {
//
//    override fun toSQL(queryBuilder: QueryBuilder) = "${expr1.toSQL(queryBuilder)}-${expr2.toSQL(queryBuilder)}"
//}


/**
 * https://www.geodatasource.com/developers/postgresql
 */
class DistanceOp<T, S : T>(
    private val lat1: Expression<T>,
    private val long1: Expression<S>,
    private val lat2: Double,
    private val long2: Double,
    override val columnType: IColumnType,
    private val distUnits: DistanceUnits = DistanceUnits.Kilometers
) : ExpressionWithColumnType<T>() {
    override fun toSQL(queryBuilder: QueryBuilder) =
        "${DbFunctions.FUNCTION_CALC_DIST}(${lat1.toSQL(queryBuilder)},${long1.toSQL(queryBuilder)},$lat2,$long2,'${distUnits.abbrv}')"
//            .also {
//               transaction {
//                   SQLStatement("SELECT id, $it as dist FROM POSTS;").execAndMap { rs ->
//                       println("PostID = ${rs.getLong("id")}, dist= ${rs.getFloat("dist")}")
//                   }
//               }
//            }
}

fun SizedIterable<PostDao>.orderByDistance(userLatLng: LatLng): SizedIterable<PostDao> {
    val distanceOp = DistanceOp(
        PostsTable.geoLatitude,
        PostsTable.geoLongitude,
        userLatLng.latitude,
        userLatLng.longitude,
        PostsTable.geoLatitude.columnType
    )
    return orderBy(distanceOp to SortOrder.ASC)
}


fun SizedIterable<PostDao>.orderPosts(distanceOp: DistanceOp<BigDecimal, BigDecimal>) =
    orderBy(distanceOp to SortOrder.ASC, PostsTable.date to SortOrder.DESC)

fun SizedIterable<PostDao>.orderByDistance(distanceOp: DistanceOp<BigDecimal, BigDecimal>) =
    orderBy(distanceOp to SortOrder.ASC)

fun Query.orderByDistance(userLatLng: LatLng): Query {
    val orderBy = DistanceOp(
        PostsTable.geoLatitude,
        PostsTable.geoLongitude,
        userLatLng.latitude,
        userLatLng.longitude,
        PostsTable.geoLatitude.columnType
    ) to SortOrder.ASC
    (orderByExpressions as MutableList).add(orderBy)
    return this
}