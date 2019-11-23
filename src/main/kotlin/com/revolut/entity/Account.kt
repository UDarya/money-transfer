package com.revolut.entity

import com.revolut.table.Accounts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

class Account(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Account>(Accounts)

    var accountId by Accounts.id
    var balance by Accounts.cents
}