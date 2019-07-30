package org.iheartradio.techtalk.domain.dao

import org.iheartradio.techtalk.domain.entity.PostsTable
import org.iheartradio.techtalk.domain.entity.UsersTable
import org.iheartradio.techtalk.shared.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

class UserDao(id: EntityID<Long>) : LongEntity(id) {
    var username by UsersTable.username
    var password_hash by UsersTable.password_hash
    var jwt by UsersTable.jwt
    val posts by PostDao referrersOn PostsTable.user

    companion object : LongEntityClass<UserDao>(UsersTable)
}

fun UserDao.toUser() = User(id = id.value, username = username, password = password_hash, jwt = jwt ?: "")


/**
 * EXAMPLE HOW TO USE REFERENCES
 */
//
//object Messages : IntIdTable() {
//    val customer = reference("customer", Customers)
//    val body = varchar(name = "BODY", length = POST_MAX_CHARS)
//    val date = datetime(name = "DATE")
//}
//
//class Message(id: EntityID<Int>) : IntEntity(id) {
//    companion object : IntEntityClass<Message>(Messages)
//    var body by Messages.body
//    var customer by Customer referencedOn Messages.customer
//    var date by Messages.date
//}
//
//
//object Customers: IntIdTable() {
//    val name = varchar("name", 50)
//}
//
//class Customer(id: EntityID<Int>) : IntEntity(id) {
//    companion object : IntEntityClass<Customer>(Customers)
//    var name by Customers.name
//    val messages by Message referrersOn Messages.customer
//}