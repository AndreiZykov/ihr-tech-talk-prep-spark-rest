package org.iheartradio.techtalk.repo

interface Cache<T> {
    fun all(): List<T>
    fun findById(localUserId: Long): T?
}