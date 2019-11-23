package com.revolut.dao

import com.revolut.entity.Account
import org.jetbrains.exposed.sql.transactions.transaction

class AccountDAOImpl: AccountDAO {

    override fun createAccount(): Account {
        return transaction {
            Account.new {
                balanceCents = 0L
            }
        }
    }

    override fun getById(id: Long): Account? {
        return transaction {
            Account.findById(id)
        }
    }

    override fun update(id: Long, cents: Long): Account? {
        return transaction {
            val account = Account.findById(id)
            account?.balanceCents = cents
            return@transaction account
        }
    }
}