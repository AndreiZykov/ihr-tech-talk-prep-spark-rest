package org.iheartradio.techtalk.domain.entity

import org.jetbrains.exposed.dao.LongIdTable

object PostExtrasTable : LongIdTable() {
    //not the user id attach to the post in question but the user who is liking or reposting this post
    val userId = long("USER_ID").primaryKey()
    //the post id being interacted with (like/dislike/repost)
    val postId = long("POST_ID").primaryKey()
    // 1 for like, -1 for dislike, 0 for neutral
    val like = integer("LIKE")
    //1 for reposted or 0 no neutral
    val repost = integer("REPOST")
}