package com.revolut.dao

import com.revolut.entity.Account

interface AccountDAO {

    fun createAccount(): Account

    fun getById(id: Long): Account?

    fun update(id: Long, cents: Long): Account?
}