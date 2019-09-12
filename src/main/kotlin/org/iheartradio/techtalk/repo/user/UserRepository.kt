package org.iheartradio.techtalk.repo.user

import kotlinx.nosql.*
import org.iheartradio.techtalk.domain.dao.UserDao
import org.iheartradio.techtalk.domain.dao.toUser
import org.iheartradio.techtalk.domain.entity.Users
import org.iheartradio.techtalk.domain.withMongoDbSession
import org.iheartradio.techtalk.model.User
import org.iheartradio.techtalk.repo.Cache
import org.iheartradio.techtalk.repo.Repository
import org.iheartradio.techtalk.repo.SqlService
import org.iheartradio.techtalk.utils.extensions.isGreaterThan
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.sql.transactions.transaction


class UserSqlService(val dao: LongEntityClass<UserDao>) : SqlService<User> {
    override fun all(): List<User> = transaction { dao.all().map { it.toUser() } }
    override fun selectById(id: Long): User? = transaction { UserDao.findById(id) }?.toUser()
}

class UserCache : Cache<User> {
//    override fun findById(localUserId: Long): User? = withMongoDbSession {
//        Session.threadLocale.set(this)
//        val user = Users.find { this.idLong.equal(localUserId) }.singleOrNull()
//        Session.threadLocale.set(null)
//        return@withMongoDbSession user
//    }

    override fun all(): List<User> = withMongoDbSession {
        Users.find { idLong isGreaterThan 0 }
            .toList()
    } ?: emptyList()


    override fun findById(localUserId: Long): User? = withMongoDbSession {

        //        Users.execute { Session.threadLocale.set(this) }
////            .find { this.idLong.equal(localUserId) }.singleOrNull()
//            .fetchSingle { this.idLong.equal(localUserId) }
//            .also { Session.threadLocale.set(null) }


        val users = Users//.execute { Session.threadLocale.set(this) }
//            .fetchList({ GreaterQuery(idLong, LiteralExpression(1)) }, 1, 10)
//            .find{ GreaterQuery(idLong, LiteralExpression(1))}
//            .find { this.idLong.gt(1) }
            .find {
                Session.threadLocale.set(this@withMongoDbSession)
                idLong isGreaterThan localUserId
            }
            .also { usersMatchingPredicate ->
                usersMatchingPredicate.forEach { user ->
                    println("findById($localUserId) USER = $user")
                }
            }


        users.firstOrNull()
            ?.also { Session.threadLocale.set(null) }
    }
}

class UserRepository {
    companion object : Repository<User>(UserSqlService(UserDao), UserCache())
}

fun <T : DocumentSchema<P, C>, P : Any, C : Any> T.fetchSingle(query: T.() -> Query): C? = withMongoDbSession {
    val params = DocumentSchemaQueryParams(this@fetchSingle, query())
    DocumentSchemaQueryWrapper(params).singleOrNull()
}


fun <T : DocumentSchema<P, C>, P : Any, C : Any> T.fetchList(
    query: T.() -> Query,
    page: Int = 1,
    pageItemCount: Int = 10
): List<C>? = withMongoDbSession {
    val params = DocumentSchemaQueryParams(this@fetchList, query())
    DocumentSchemaQueryWrapper(params)
//        .paginate(page, pageItemCount)
        .toList()
}

//fun <T: DocumentSchema<P, C>, P: Any, C: Any> T.find2(query: T.() -> Query): DocumentSchemaQueryWrapper<T, P, C> {
//    withMongoDbSession {
//        Session.threadLocale.set(this)
//        DocumentSchemaQueryParams(this@find2, query())
//
//    }.let { params ->
//        DocumentSchemaQueryWrapper(params)
//    }
//
//}
