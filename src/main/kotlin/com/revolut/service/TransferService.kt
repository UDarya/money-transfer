package com.revolut.service

import com.revolut.enums.Status

class TransferService {

    fun transfer(from: String, to: String): Status {
        return Status.SUCCESS
    }
}