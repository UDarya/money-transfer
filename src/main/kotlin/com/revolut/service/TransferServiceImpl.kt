package com.revolut.service

import com.revolut.dao.AccountDAO
import com.revolut.entity.Account
import com.revolut.enums.Status
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class TransferServiceImpl(val accountDAO: AccountDAO) : TransferService {

    override fun transfer(from: Long, to: Long, amount: Long): Status {
        return transaction(Connection.TRANSACTION_SERIALIZABLE, 2) {
            var fromAcc: Account? = accountDAO.getById(from) ?: return@transaction Status.THERE_IS_NOT_ACCOUNT
            var tooAcc: Account? = accountDAO.getById(to) ?: return@transaction Status.THERE_IS_NOT_ACCOUNT
            return@transaction Status.SUCCESS
        }
    }
}