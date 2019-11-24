package com.home.dao

import com.home.table.Accounts
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AccountDAOTest {

    private val accountDAO: AccountDAOImpl = AccountDAOImpl()

    @Test
    fun `when account created then select result is not null`() {
        val account = accountDAO.createAccount()

        assertNotNull(account)
        assertNotNull(account.accountId)
        assertNotNull(account.balanceCents)

        val resultAccount = accountDAO.getById(account.accountId.value)

        assertNotNull(resultAccount)
        assertEquals(0L, resultAccount.balanceCents)
    }

    @Test
    fun `when account is updated then new balance value`() {
        val account = accountDAO.createAccount()
        val cents = 1000L
        accountDAO.update(account.accountId.value, cents)
        val resultAccount = accountDAO.getById(account.accountId.value)

        assertNotNull(resultAccount)
        assertEquals(cents, resultAccount.balanceCents)

    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun `run db`() {
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
            transaction {
                SchemaUtils.create(Accounts)
            }
        }
    }
}