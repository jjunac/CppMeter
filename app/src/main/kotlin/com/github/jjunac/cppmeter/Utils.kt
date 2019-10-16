package com.github.jjunac.cppmeter

import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection


public fun <T> sqliteTransaction(statement: org.jetbrains.exposed.sql.Transaction.() -> T): T =
        transaction(Connection.TRANSACTION_READ_UNCOMMITTED, 1) { statement() }
