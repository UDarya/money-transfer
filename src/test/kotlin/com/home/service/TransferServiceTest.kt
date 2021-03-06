package com.home.service

import com.home.dao.AccountDAOImpl
import com.home.entity.Account
import com.home.enums.Status
import com.home.table.Accounts
import io.mockk.spyk
import io.mockk.verifySequence
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class TransferServiceTest {
    private val accountDAO = spyk(AccountDAOImpl())
    private val transferService = TransferServiceImpl(accountDAO)

    @Test
    fun `when first account doesn't exist then error`() {
        val fromAcc = 1L
        val tooAcc = 2L

        val status = transferService.transfer(fromAcc, tooAcc, 100)

        assertNotNull(status)
        assertEquals(Status.THERE_IS_NOT_ACCOUNT, status)
    }

    @Test
    fun `when second account doesn't exist then error`() {
        val fromAcc = 1L
        val toAcc = 2L

        transaction {
            Account.new {
                this.accountId = EntityID(fromAcc, Accounts)
                this.balanceCents = 0
            }
        }

        val status = transferService.transfer(fromAcc, toAcc, 100)

        assertNotNull(status)
        assertEquals(Status.THERE_IS_NOT_ACCOUNT, status)
    }

    @Test
    fun `when amount more than balance then error`() {
        val fromAcc = 1L
        val toAcc = 2L

        transaction {
            Account.new {
                this.accountId = EntityID(fromAcc, Accounts)
                this.balanceCents = 50
            }

            Account.new {
                this.accountId = EntityID(toAcc, Accounts)
                this.balanceCents = 50
            }
        }

        val status = transferService.transfer(fromAcc, toAcc, 100)

        assertNotNull(status)
        assertEquals(Status.NOT_ENOUGH_MONEY, status)
    }

    @Test
    fun `when transfer 100 from account with 200 then success`() {
        val fromAcc = 1L
        val toAcc = 2L

        val amountCents = 200 * 100L
        val transferAmount = 100L

        transaction {
            Account.new {
                this.accountId = EntityID(fromAcc, Accounts)
                this.balanceCents = amountCents
            }

            Account.new {
                this.accountId = EntityID(toAcc, Accounts)
                this.balanceCents = amountCents
            }
        }

        val status = transferService.transfer(fromAcc, toAcc, transferAmount)

        assertNotNull(status)
        assertEquals(Status.SUCCESS, status)

        val fromAccount = accountDAO.getById(fromAcc)
        assertNotNull(fromAccount)
        assertEquals(amountCents - transferAmount * 100, fromAccount.balanceCents)

        val toAccount = accountDAO.getById(toAcc)
        assertNotNull(toAccount)
        assertEquals(amountCents + transferAmount * 100, toAccount.balanceCents)
    }

    @Test
    fun `when negative then return incorrect amount error`() {
        val status = transferService.transfer(1L, 2L, -5)

        assertNotNull(status)
        assertEquals(Status.INCORRECT_AMOUNT, status)
    }

    @Test
    fun `when transfer from 2 to 1 then first select for 1`() {
        val fromAcc = 2L
        val toAcc = 1L

        transaction {
            Account.new {
                this.accountId = EntityID(fromAcc, Accounts)
                this.balanceCents = 100 * 100
            }

            Account.new {
                this.accountId = EntityID(toAcc, Accounts)
                this.balanceCents = 100 * 100
            }
        }

        val status = transferService.transfer(fromAcc, toAcc, 100)
        assertNotNull(status)
        assertEquals(Status.SUCCESS.code, status.code)

        verifySequence {
            accountDAO.getById(toAcc)
            accountDAO.getById(fromAcc)
        }
    }

    @Before
    fun `run db`() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(Accounts)
        }
        transaction {
            Accounts.deleteAll()
        }
    }
}