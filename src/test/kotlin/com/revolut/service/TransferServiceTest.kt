package com.revolut.service

import com.revolut.enums.Status
import org.junit.Test
import kotlin.test.assertEquals

class TransferServiceTest {

    @Test
    fun `success transfer`() {
        assertEquals(Status.SUCCESS, TransferService().transfer("1", "2"))
    }
}