package com.revolut

import com.revolut.controller.MoneyTransferController
import spark.Spark


private const val HTTP_PORT = 8000

fun main() {
    Spark.port(HTTP_PORT)
    MoneyTransferController()
}


