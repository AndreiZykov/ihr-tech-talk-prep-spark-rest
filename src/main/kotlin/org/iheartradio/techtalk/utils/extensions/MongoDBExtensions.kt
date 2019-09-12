@file:JvmName("MongoDBExtensions")
package org.iheartradio.techtalk.utils.extensions

import com.mongodb.BasicDBObjectBuilder
import com.mongodb.DB
import com.mongodb.DBCollection
import kotlinx.coroutines.runBlocking
import kotlinx.nosql.*
import kotlinx.nosql.mongodb.MongoDBSession
import java.lang.Exception


const val MONGO_DB_TABLE_USERS = "users"
const val MONGO_DB_TABLE_POSTS = "posts"

val DB.users get() = getCollection(MONGO_DB_TABLE_USERS)

val DB.posts get() = getCollection(MONGO_DB_TABLE_POSTS)


fun DBCollection.findByPrimaryId(id: Long) = try {
    find(
        BasicDBObjectBuilder.start()
            .append("idLong", id)
            .get()
    ).singleOrNull()
} catch (e: Exception) {
    e.printStackTrace()
    null
}


// TODO: Implement find(id) -> Wrapper
/*fun <T: DocumentSchema<P, C>, P, C> T.find(id: Id<P, T>): C? {
    val w = find { this.id.equal(id) }
    return if (w.count() > 0) w.single() else null
}*/

fun < T: DocumentSchema<P, C>, S: AbstractSchema, P:Any, C: Any> MongoDBSession.find(schema: T, id: AbstractColumn<C, S, T>, value: Any) {
    find(DocumentSchemaQueryParams(schema, id.equal(value)))
}


fun <T: DocumentSchema<P, C>, P: Any, C: Any, S: Any> MongoDBSession.find(pair: Pair<AbstractColumn<C, T, S>, Any>) {
    find(DocumentSchemaQueryParams(pair.first.schema, pair.first.equal(pair.second)))
}