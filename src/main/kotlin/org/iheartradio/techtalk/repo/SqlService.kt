package org.iheartradio.techtalk.repo

interface SqlService<T> {
    fun all(): List<T>
    fun selectById(id: Long): T?
}