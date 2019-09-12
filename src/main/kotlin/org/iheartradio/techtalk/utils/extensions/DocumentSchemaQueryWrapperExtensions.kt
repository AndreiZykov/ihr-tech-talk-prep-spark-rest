@file:JvmName("DocumentSchemaQueryWrapperExtensions")

package org.iheartradio.techtalk.utils.extensions

import kotlinx.nosql.*
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.query.EqualQuery
import org.iheartradio.techtalk.domain.DB
import org.iheartradio.techtalk.domain.entity.Users

/*
fun <T> SizedIterable<T>.paginate(
    page: Int = 1,
    pageItemCount: Int = 5
) = limit(
    pageItemCount,
    offset = ((page - 1) * pageItemCount)
)
 */

fun <T : DocumentSchema<P, C>, P : Any, C : Any> DocumentSchemaQueryWrapper<T, P, C>.paginate(
    page: Int,
    pageItemCount: Int
): DocumentSchemaQueryWrapper<T, P, C> = take(pageItemCount).skip(((page - 1) * pageItemCount))