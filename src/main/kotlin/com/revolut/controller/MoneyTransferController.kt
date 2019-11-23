package com.revolut.controller

import com.google.gson.Gson
import com.revolut.enums.Status
import com.revolut.service.TransferRq
import com.revolut.service.TransferService
import com.revolut.validator.isValid
import org.eclipse.jetty.http.HttpStatus
import spark.Response
import spark.Spark.post

class MoneyTransferController(transferService: TransferService) {
    init {
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
                    response.status(HttpStatus.Code.OK.code)
                }
                else -> {
                    response.body(status.description)
                    response.status(HttpStatus.Code.INTERNAL_SERVER_ERROR.code)
                }
            }

            response.body()
        }
    }

    private fun badRequestRs(response: Response): String {
        response.status(HttpStatus.Code.BAD_REQUEST.code)
        response.body(HttpStatus.Code.BAD_REQUEST.message)
        return HttpStatus.Code.BAD_REQUEST.message
    }
}