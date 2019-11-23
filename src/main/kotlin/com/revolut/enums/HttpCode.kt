package com.revolut.enums

enum class HttpCode(val code: Int, val description: String) {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad request"),
    SERVER_ERROR(500, "Server error")
}