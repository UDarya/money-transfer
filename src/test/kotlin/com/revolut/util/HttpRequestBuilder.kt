package com.revolut.util

import okhttp3.Response

fun doHttpPost(jsonBody: String, host: String, port: Int, path: String): Response =
    io.github.rybalkinsd.kohttp.dsl.httpPost {
        this.host = host
        this.port = port
        this.path = path
        body {
            json(jsonBody)
        }
    }