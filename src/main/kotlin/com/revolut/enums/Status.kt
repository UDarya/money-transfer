package com.revolut.enums

enum class Status(val code: Int, val description: String) {
    SUCCESS(1, "Success"),
    ERROR(2, "Error"),
    THERE_IS_NOT_ACCOUNT(3, "Account does not exist")

}