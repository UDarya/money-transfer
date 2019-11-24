package com.home.service

data class TransferRq(
    val from: Long?,
    val to: Long?,
    val cents: Long?
)