package com.example.test.data.repository

import com.example.test.data.local.dao.TransactionDao
import com.example.test.data.local.mappers.toDomain
import com.example.test.data.local.mappers.toEntity
import com.example.test.domain.model.Transaction
import com.example.test.domain.model.TransactionType
import com.example.test.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getTransactionById(id: Long): Flow<Transaction?> {
        return transactionDao.getTransactionById(id).map { it?.toDomain() }
    }

    override fun getTransactionsByDateRange(startDateMillis: Long, endDateMillis: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDateMillis, endDateMillis).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type.name).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getRecurringTransactions(): Flow<List<Transaction>> {
        return transactionDao.getRecurringTransactions().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    override suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }

    override fun getTotalExpenseForPeriod(startDateMillis: Long, endDateMillis: Long): Flow<Double> {
        return transactionDao.getTotalExpenseForPeriod(startDateMillis, endDateMillis).map { it ?: 0.0 }
    }

    override fun getTotalIncomeForPeriod(startDateMillis: Long, endDateMillis: Long): Flow<Double> {
        return transactionDao.getTotalIncomeForPeriod(startDateMillis, endDateMillis).map { it ?: 0.0 }
    }

    override fun getTotalExpense(): Flow<Double> {
        return transactionDao.getTotalExpense().map { it ?: 0.0 }
    }

    override fun getTotalIncome(): Flow<Double> {
        return transactionDao.getTotalIncome().map { it ?: 0.0 }
    }

    override fun getCategoryExpenseForPeriod(categoryName: String, startDateMillis: Long, endDateMillis: Long): Flow<Double> {
        return transactionDao.getCategoryExpenseForPeriod(categoryName, startDateMillis, endDateMillis).map { it ?: 0.0 }
    }
}
