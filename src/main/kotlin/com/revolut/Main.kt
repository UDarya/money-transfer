package com.revolut

import com.revolut.controller.MoneyTransferController
import com.revolut.dao.AccountDAO
import com.revolut.dao.AccountDAOImpl
import com.revolut.service.TransferService
import com.revolut.service.TransferServiceImpl
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module.applicationContext
import org.koin.spark.controller
import org.koin.spark.runControllers
import org.koin.spark.start


private const val HTTP_PORT = 8000

val moneyModule = applicationContext {
    bean { AccountDAOImpl() as AccountDAO }
    bean { TransferServiceImpl(get()) as TransferService }
    controller { MoneyTransferController(get()) }
}

fun main() {
    start(modules = listOf(moneyModule), port = HTTP_PORT) {
        runControllers()
    }
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
}


