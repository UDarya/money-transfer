package com.revolut.controller

import com.revolut.enums.HttpCode
import com.revolut.enums.Status
import com.revolut.service.TransferService
import spark.Spark.post

class MoneyTransferController {
    init {
        val transferService = TransferService()

        post("/transfer") { request, response ->
            val from = request.queryParams("from")
            val to = request.queryParams("to")

            when(val status = transferService.transfer(from, to)) {
                Status.SUCCESS -> {
                    response.body(status.description)
                    response.status(HttpCode.OK.code)
                }
                else -> {
                    response.body(status.description)
                    response.status(HttpCode.SERVER_ERROR.code)
                }

            }

            response.body()
        }
    }
}