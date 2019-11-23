package com.revolut.table

import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column

object Accounts : LongIdTable("ACCOUNTS") {
    val cents: Column<Long> = long("cents")
}