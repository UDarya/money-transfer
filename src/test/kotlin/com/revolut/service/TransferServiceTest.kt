package com.revolut.service

import com.revolut.dao.AccountDAO
import com.revolut.entity.Account
import com.revolut.enums.Status
import com.revolut.table.Accounts
import io.mockk.every
import io.mockk.mockk
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class TransferServiceTest {
    private val accountDAOMock: AccountDAO = mockk()
    private val transferService = TransferServiceImpl(accountDAOMock)

    @Test
    fun `when first account doesn't exist then error`() {
        val fromAcc = 1L
        val tooAcc = 2L

        every {
            accountDAOMock.getById(fromAcc)
        } returns null

        val status = transferService.transfer(fromAcc, tooAcc, 100)

        assertNotNull(status)
        assertEquals(Status.THERE_IS_NOT_ACCOUNT, status)
    }

    @Test
    fun `when second account doesn't exist then error`() {
        val fromAcc = 1L
        val toAcc = 2L

        every {
            accountDAOMock.getById(fromAcc)
        } returns Account(EntityID(1L, LongIdTable()))

        every {
            accountDAOMock.getById(toAcc)
        } returns null

        val status = transferService.transfer(fromAcc, toAcc, 100)

        assertNotNull(status)
        assertEquals(Status.THERE_IS_NOT_ACCOUNT, status)
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