package com.revolut.money

fun Long.toCents(): Long = this.times(100)