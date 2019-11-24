package com.revolut.enums

enum class Status(val code: Int, val description: String) {
    SUCCESS(1, "Success"),
    ERROR(2, "Error"),
    THERE_IS_NOT_ACCOUNT(3, "Account does not exist"),
    NOT_ENOUGH_MONEY(4, "not enough money"),
    INCORRECT_AMOUNT(5, "Amount should be more than 0")
}