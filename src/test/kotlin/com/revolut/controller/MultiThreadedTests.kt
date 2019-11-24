package com.revolut.controller

import com.google.gson.Gson
import com.revolut.entity.Account
import com.revolut.money.toCents
import com.revolut.moneyModule
import com.revolut.service.TransferRq
import com.revolut.table.Accounts
import com.revolut.util.doHttpPost
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.spark.runControllers
import org.koin.spark.start
import org.koin.spark.stop
import repeat.Repeat
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import repeat.RepeatRule



private const val PORT = 8000
private const val HOST = "localhost"
private const val PATH = "/transfer"
private val gson = Gson()

class MultiThreadedTests {

    @Rule
    @JvmField
    var rule = RepeatRule()

    @Test
    @Repeat(times = 10)
    fun `when 100 async request then data is consistent`() {
        val fromAcc = 1L
        val toAcc = 2L
        val amount = 1L

        val fromAmountCents = 105L.toCents()
        val toAmountCents = 200L.toCents()

        val numOfTransfers = 100

        var request = TransferRq(fromAcc, toAcc, amount)

        createAccounts(fromAcc, fromAmountCents, toAcc, toAmountCents)

        runBlocking {
            List(numOfTransfers) {
                launch {
                    doHttpPost(gson.toJson(request), HOST, PORT, PATH)
                }
            }
        }

        var resultFromAcc: Account? = null
        var resultToAcc: Account? = null

        transaction {
            resultFromAcc = Account.findById(fromAcc)
            resultToAcc = Account.findById(toAcc)
        }

        assertNotNull(resultFromAcc)
        assertNotNull(resultToAcc)

        assertEquals(fromAmountCents - numOfTransfers * 100, resultFromAcc!!.balanceCents)
        assertEquals(toAmountCents + numOfTransfers * 100, resultToAcc!!.balanceCents)
    }

    private fun createAccounts(fromAcc: Long, fromAmountCents: Long, toAcc: Long, toAmountCents: Long) {
        transaction {
            Account.new {
                this.accountId = EntityID(fromAcc, Accounts)
                this.balanceCents = fromAmountCents
            }

            Account.new {
                this.accountId = EntityID(toAcc, Accounts)
                this.balanceCents = toAmountCents
            }
        }
    }


    @Before
    fun `run application`() {
        start(PORT, modules = listOf(moneyModule)) {
            runControllers()
        }
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(Accounts)
            Accounts.deleteAll()
        }
    }

    @After
    fun `stop application`() {
        stop()
    }
}