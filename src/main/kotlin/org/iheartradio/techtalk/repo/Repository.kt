package org.iheartradio.techtalk.repo

import org.iheartradio.techtalk.utils.ErrorType
import org.iheartradio.techtalk.utils.apiException
import org.iheartradio.techtalk.utils.extensions.ifNullThen
import org.iheartradio.techtalk.utils.extensions.orElse

open class Repository<T>(private val sql: SqlService<T>, private val cache: Cache<T>) {

    private fun getFromCache(localUserId: Long) = sql.selectById(localUserId)
    private fun getFromDb(localUserId: Long) = cache.findById(localUserId)

    fun fetch(localUserId: Long): T? = getFromCache(localUserId)
        .orElse(getFromDb(localUserId))
        .ifNullThen { apiException(ErrorType.USER_NOT_FOUND) }
}