package org.iheartradio.techtalk

import org.jetbrains.exposed.sql.transactions.transaction

inline class SQLStatement(val sql: String) {
    fun exec() {
        transaction {
            exec(sql)
        }
    }
}