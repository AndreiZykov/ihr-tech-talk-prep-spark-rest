package org.iheartradio.techtalk.domain.entity

import com.mongodb.BasicDBObjectBuilder
import kotlinx.nosql.*
import kotlinx.nosql.PrimaryKey.Companion.string
import kotlinx.nosql.mongodb.DocumentSchema
import org.bson.types.ObjectId
import org.iheartradio.techtalk.domain.DB
import org.iheartradio.techtalk.model.User
import org.iheartradio.techtalk.utils.extensions.findByPrimaryId
import org.iheartradio.techtalk.utils.extensions.users
import org.jetbrains.exposed.dao.LongIdTable

object UsersTable : LongIdTable() {
    val username = varchar(name = "USER_NAME", length = 100).uniqueIndex()
    val password_hash = varchar(name = "PASSWORD_HASH", length = 500)
    val jwt = varchar(name = "JWT", length = 500).nullable().index()
    val posts = reference("posts", PostsTable).nullable()
}


object Users : DocumentSchema<User>("users", User::class) {
    val idLong = long("idLong")
    val username = string("username")
    val password = string("password")
    val jwt = nullableString("jwt")
    val posts = listOfId("posts", Posts)



//    fun select(query: Users.() -> Query) : User = DB.withSession {
//       find {  }
//    }
    fun findById(id: Long): User? = DB.withSession {
        this.db.users.findByPrimaryId(id)
            ?.let {
                println("this.db.getCollection(\"users\").find(query) result")
                println("DBObject = $it")
                println("DBObject.class = ${it::class.java.simpleName}")
                println("DBObject.toMap() = ${it.toMap()}")
                val id = it["_id"] as ObjectId
                val userId = it["idLong"] as Long
                val username = it["username"] as String
                val jwt = it["jwt"] as String?
                User(
                    id = Id(id.toHexString()),
                    idLong = userId,
                    username = username,
                    password = "",
                    jwt = jwt
                )
            }
    }
}




