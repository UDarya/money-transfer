package com.revolut.service

data class TransferRq(
    val from: Long?,
    val to: Long?,
    val cents: Long?
)