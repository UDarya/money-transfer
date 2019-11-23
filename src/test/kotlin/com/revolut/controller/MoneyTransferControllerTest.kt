package com.revolut.controller

import com.google.gson.Gson
import com.revolut.dao.AccountDAO
import com.revolut.enums.Status
import com.revolut.service.TransferRq
import com.revolut.service.TransferService
import io.github.rybalkinsd.kohttp.dsl.httpPost
import io.mockk.every
import io.mockk.mockk
import okhttp3.Response
import org.eclipse.jetty.http.HttpStatus
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.spark.start
import org.koin.spark.stop
import org.koin.test.KoinTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


private const val PORT = 8000
private const val HOST = "localhost"
private const val PATH = "/transfer"
private val gson = Gson()

class MoneyTransferControllerTest : KoinTest {

    private val transferServiceMock: TransferService = mockk()
    private val accountDAO: AccountDAO = mockk()

    @Test
    fun `success post request`() {
        var request = TransferRq(1, 1, 1)

        every {
            transferServiceMock.transfer(any(), any(), any())
        } returns Status.SUCCESS

        val response: Response = httpPost(gson.toJson(request))

        assertNotNull(response)
        assertTrue(response.isSuccessful)
    }

    @Test
    fun `when empty request body then return bad request error`() {
        val response: Response = httpPost("")

        assertNotNull(response)
        assertTrue(!response.isSuccessful)
        assertEquals(HttpStatus.Code.BAD_REQUEST.code, response.code())
    }

    @Test
    fun `when parameters are empty then return bad request error`() {
        var request = TransferRq(null, 2, 1)
        var response: Response = httpPost(gson.toJson(request))

        assertNotNull(response)
        assertTrue(!response.isSuccessful)
        assertEquals(HttpStatus.Code.BAD_REQUEST.code, response.code())

        request = TransferRq(2, null, 1)
        response = httpPost(gson.toJson(request))

        assertNotNull(response)
        assertTrue(!response.isSuccessful)
        assertEquals(HttpStatus.Code.BAD_REQUEST.code, response.code())

        request = TransferRq(2, 1, null)
        response = httpPost(gson.toJson(request))

        assertNotNull(response)
        assertTrue(!response.isSuccessful)
        assertEquals(HttpStatus.Code.BAD_REQUEST.code, response.code())
    }

    @Test
    fun `when request format incorrect then return bad request error`() {
        val request = "{\"test\": \"incorrect request\"}"
        var response: Response = httpPost(request)

        assertNotNull(response)
        assertTrue(!response.isSuccessful)
        assertEquals(HttpStatus.Code.BAD_REQUEST.code, response.code())
    }

    @Test
    fun `when there is not account then return error`() {
        var request = TransferRq(1, 1, 1)

        every {
            transferServiceMock.transfer(any(), any(), any())
        } returns Status.THERE_IS_NOT_ACCOUNT

        val response: Response = httpPost(gson.toJson(request))

        assertNotNull(response)
        assertTrue(!response.isSuccessful)
        assertEquals(HttpStatus.Code.INTERNAL_SERVER_ERROR.code, response.code())
        assertNotNull(response.body())
        assertEquals(Status.THERE_IS_NOT_ACCOUNT.description, response.body()!!.string())
    }

    private fun httpPost(jsonBody: String): Response = httpPost {
        host = HOST
        port = PORT
        path = PATH
        body {
            json(jsonBody)
        }
    }


    @Before
    fun `run application`() {
        start(PORT, modules = listOf(moneyMockModule)) {
            MoneyTransferController(transferServiceMock)
        }
    }

    @After
    fun `stop application`() {
        stop()
    }

    private val moneyMockModule = applicationContext {
        bean { accountDAO }
        bean { transferServiceMock }
    }

}