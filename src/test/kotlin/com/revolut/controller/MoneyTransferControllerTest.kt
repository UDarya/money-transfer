package com.revolut.controller

import com.google.gson.Gson
import com.revolut.service.TransferRq
import io.github.rybalkinsd.kohttp.dsl.httpPost
import okhttp3.Response
import org.junit.BeforeClass
import org.junit.Test
import spark.kotlin.port
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


private const val PORT = 8000
private const val HOST = "localhost"
private const val PATH = "/transfer"
private val gson = Gson()

class MoneyTransferControllerTest {

    @Test
    fun `success post request`() {
        var request = TransferRq(1, 1, 1)
        val response: Response = httpPost(gson.toJson(request))

        assertNotNull(response)
        assertTrue(response.isSuccessful)
    }

    @Test
    fun `when empty request body then bad request error`() {
        val response: Response = httpPost("")

        assertNotNull(response)
        assertTrue(!response.isSuccessful)
    }

    @Test
    fun `when parameters are empty then bad request error`() {
        var request = TransferRq(null, 2, 1)
        var response: Response = httpPost(gson.toJson(request))

        assertNotNull(response)
        assertTrue(!response.isSuccessful)

        request = TransferRq(2, null, 1)
        response = httpPost(gson.toJson(request))

        assertNotNull(response)
        assertTrue(!response.isSuccessful)

        request = TransferRq(2, 1, null)
        response = httpPost(gson.toJson(request))

        assertNotNull(response)
        assertTrue(!response.isSuccessful)
    }

    private fun httpPost(jsonBody: String): Response = httpPost {
        host = HOST
        port = PORT
        path = PATH
        body {
            json(jsonBody)
        }
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun `run application`() {
            port(PORT)
            MoneyTransferController()
        }
    }
}