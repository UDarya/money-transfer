package com.home.validator

import com.home.service.TransferRq

fun TransferRq?.isValid(): Boolean  = (this != null && from != null && to != null && cents != null)