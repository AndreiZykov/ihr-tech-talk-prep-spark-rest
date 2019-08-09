package org.iheartradio.techtalk.utils.extensions

import org.jetbrains.exposed.sql.SizedIterable

fun <T> SizedIterable<T>.paginate(page : Int = 1,
                                  pageItemCount: Int = 5) = limit(pageItemCount,
    offset = ((page-1) * pageItemCount))


fun <T> SizedIterable<T>.isNotEmpty() = !empty()