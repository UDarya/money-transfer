package com.revolut.validator

import com.revolut.service.TransferRq

fun TransferRq?.isValid(): Boolean  = (this != null && from != null && to != null && cents != null)