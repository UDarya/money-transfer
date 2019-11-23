package com.revolut

import com.revolut.controller.MoneyTransferController
import org.jetbrains.exposed.sql.Database
import spark.Spark


private const val HTTP_PORT = 8000

fun main() {
    Spark.port(HTTP_PORT)
    MoneyTransferController()
    // todo
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
}


