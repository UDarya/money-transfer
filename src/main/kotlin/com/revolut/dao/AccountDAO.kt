package com.revolut.dao

import com.revolut.entity.Account
import org.jetbrains.exposed.sql.transactions.transaction

class AccountDAO {

    fun createAccount(): Account {
        return transaction {
            Account.new {
                balance = 0L
            }
        }
    }

    fun getById(id: Long): Account? {
        return transaction {
            Account.findById(id)
        }
    }

    fun update(id: Long, cents: Long): Account? {
        return transaction {
            val account = Account.findById(id)
            account?.balance = cents
            return@transaction account
        }
    }
}