package com.revolut.service

import com.revolut.enums.Status

interface TransferService {
    fun transfer(from: Long, to: Long, amount: Long): Status
}
