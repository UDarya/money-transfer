package com.revolut.controller

import com.revolut.main
import io.github.rybalkinsd.kohttp.dsl.httpPost
import okhttp3.Response
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull


private const val PORT = 8000

class MoneyTransferControllerTest {

    @Test
    fun `success post request`() {
        val response: Response = httpPost {
            host = "localhost"
            port = PORT
            path = "/transfer"
            body {
                form("from=1&to=2")
            }
        }

        assertNotNull(response)
        assertNotNull(response.isSuccessful)

    }

    @Before
    fun `run application`() {
        main()
    }
}