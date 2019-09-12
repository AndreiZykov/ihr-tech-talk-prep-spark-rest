package org.iheartradio.techtalk.cache

import kotlinx.nosql.Session
import kotlinx.nosql.equal
import kotlinx.nosql.id
import org.iheartradio.techtalk.domain.DB
import org.iheartradio.techtalk.domain.entity.Users
import org.iheartradio.techtalk.domain.withMongoDbSession
import org.iheartradio.techtalk.model.User
import org.iheartradio.techtalk.utils.ErrorType
import org.iheartradio.techtalk.utils.apiException
import org.jetbrains.exposed.sql.transactions.transaction

object UsersCache {

    fun findById(localUserId: Long): User? = withMongoDbSession {
        Session.threadLocale.set(this)
        Users.find { this.idLong.equal(localUserId) }
            .singleOrNull()
            .also { Session.threadLocale.set(null) }
    }

    fun findById(mongoUserIdHex: String): User? = withMongoDbSession {
        Session.threadLocale.set(this)
        Users.find { this.id.equal(mongoUserIdHex) }
            .singleOrNull()
            .also { Session.threadLocale.set(null) }
    }
}