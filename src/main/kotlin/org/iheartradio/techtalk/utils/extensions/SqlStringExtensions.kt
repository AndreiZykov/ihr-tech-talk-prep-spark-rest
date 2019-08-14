package org.iheartradio.techtalk.utils.extensions

import org.iheartradio.techtalk.SQLStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.ResultSet

//fun <T:Any> String.execAndMap(transform : (ResultSet) -> T) : List<T> {
//    val result = arrayListOf<T>()
//    TransactionManager.current().exec("") { rs ->
//        while (rs.next()) {
//            result += transform(rs)
//        }
//    }
//    return result
//}


fun <T:Any> SQLStatement.execAndMap(transform : (ResultSet) -> T) : List<T> {
    val result = arrayListOf<T>()
    TransactionManager.current().exec(sql) { rs ->
        while (rs.next()) {
            result += transform(rs)
        }
    }
    return result
}