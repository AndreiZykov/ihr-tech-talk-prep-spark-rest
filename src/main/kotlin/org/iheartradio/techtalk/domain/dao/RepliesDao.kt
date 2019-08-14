package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.domain.entity.RepliesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

class RepliesDao(id: EntityID<Long>) : LongEntity(id) {
    var postId by RepliesTable.postId
    var replyPostId by RepliesTable.replyPostId
    companion object : LongEntityClass<RepliesDao>(RepliesTable)
}