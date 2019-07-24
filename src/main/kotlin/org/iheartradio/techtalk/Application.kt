package org.iheartradio.techtalk

import com.google.gson.Gson
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Spark.path
import spark.kotlin.delete
import spark.kotlin.get
import spark.kotlin.patch
import spark.kotlin.post

const val DB_URL = "jdbc:postgresql://localhost:5432/test_db"
const val DB_DRIVER = "org.postgresql.Driver"

const val USER_NAME = "user-name"
const val PASSWORD = "password"

const val USER_PATH = "/user"

object Users : IntIdTable() {
    val name = varchar(name = "NAME", length = 50)
    val age = integer(name = "AGE")
}

class User(id: EntityID<Int>) : IntEntity(id) {
    var name by Users.name
    var age by Users.age
    companion object : IntEntityClass<User>(Users)
    infix fun update(userModel: UserModel){
        name = userModel.name
        age = userModel.age
    }
}

data class UserModel constructor(val id: Int = 0, val name: String, val age: Int) {
    companion object {
        infix fun from(jsonString: String) =
            Gson().fromJson<UserModel>(jsonString, UserModel::class.java)!!
    }
}

fun User.toUserModel() = UserModel(id = id.value, name = name, age = age)
fun UserModel.toJson() = Gson().toJson(this)!!
fun List<UserModel>.toJson() = Gson().toJson(this)!!

fun main() {
    Database.connect(url = DB_URL, driver = DB_DRIVER, user = USER_NAME, password = PASSWORD)
    transaction { SchemaUtils.createMissingTablesAndColumns(Users) }
    path(USER_PATH) {
        get("/") { transaction { User.all().map { it.toUserModel() } }.toJson() }
        post("/") {
            val user = UserModel from request.body()
            transaction { User.new { this update user } }.toUserModel().toJson()
        }
        patch("/") {
            val user = UserModel from request.body()
            transaction { User.findById(user.id)?.apply { this update user } }?.toUserModel()?.toJson()
                ?: "error"
        }
        delete("/") {
            val user = UserModel from request.body()
            transaction { User.findById(user.id)?.delete() }
            return@delete """
                {"response":"success"}
            """.trimIndent()
        }
    }
}