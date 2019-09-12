package org.iheartradio.techtalk.utils.extensions

import org.jetbrains.exposed.sql.SizedIterable

fun <T> SizedIterable<T>.paginate(
    page: Int = 1,
    pageItemCount: Int = 5
) = limit(
    pageItemCount,
    offset = ((page - 1) * pageItemCount)
)

//SizedCollection(delegate.drop(offset).take(n))
fun <T> List<T>.paginate(
    page: Int = 1,
    pageItemCount: Int = 5
) = drop((page - 1) * pageItemCount).take(pageItemCount)

fun <T> SizedIterable<T>.isNotEmpty() = !empty()