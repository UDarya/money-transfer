package com.home.service

import com.home.dao.AccountDAO
import com.home.entity.Account
import com.home.enums.Status
import com.home.money.toCents
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class TransferServiceImpl(private val accountDAO: AccountDAO) : TransferService {

    override fun transfer(from: Long, to: Long, amount: Long): Status {
        if (amount <= 0) return Status.INCORRECT_AMOUNT
        return transaction(Connection.TRANSACTION_SERIALIZABLE, 2) {
            var fromAcc: Account
            var tooAcc: Account

            if (from < to) {
                fromAcc = accountDAO.getById(from) ?: return@transaction Status.THERE_IS_NOT_ACCOUNT
                tooAcc = accountDAO.getById(to) ?: return@transaction Status.THERE_IS_NOT_ACCOUNT
            } else {
                tooAcc = accountDAO.getById(to) ?: return@transaction Status.THERE_IS_NOT_ACCOUNT
                fromAcc = accountDAO.getById(from) ?: return@transaction Status.THERE_IS_NOT_ACCOUNT
            }

            val amountCents = amount.toCents()
            val fromAccBalance = fromAcc.balanceCents
            val toAccBalance = tooAcc.balanceCents

            if (fromAccBalance < amountCents) {
                return@transaction Status.NOT_ENOUGH_MONEY
            }

            fromAcc.balanceCents = fromAccBalance - amountCents
            tooAcc.balanceCents = toAccBalance + amountCents

            return@transaction Status.SUCCESS
        }
    }
}