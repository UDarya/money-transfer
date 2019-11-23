package com.revolut.controller

import com.google.gson.Gson
import com.revolut.dao.AccountDAO
import com.revolut.enums.HttpCode
import com.revolut.enums.Status
import com.revolut.service.TransferRq
import com.revolut.service.TransferServiceImpl
import com.revolut.validator.isValid
import spark.Response
import spark.Spark.post

class MoneyTransferController {
    init {
        val transferService = TransferServiceImpl(AccountDAO())
        var gson = Gson()

        post("/transfer") { request, response ->
            val bodyStr = request.body()

            if (bodyStr == null || bodyStr.isEmpty()) {
                return@post badRequestRs(response)
            }

            val body = gson.fromJson(bodyStr, TransferRq::class.java)

            if (!body.isValid()) {
                return@post badRequestRs(response)
            }

            when (val status = transferService.transfer(body.from!!, body.to!!, body.cents!!)) {
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

    private fun badRequestRs(response: Response): String {
        response.status(HttpCode.BAD_REQUEST.code)
        response.body(HttpCode.BAD_REQUEST.description)
        return HttpCode.BAD_REQUEST.description
    }
}