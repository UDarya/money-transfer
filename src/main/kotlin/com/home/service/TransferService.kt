package com.home.service

import com.home.enums.Status

interface TransferService {
    fun transfer(from: Long, to: Long, amount: Long): Status
}
