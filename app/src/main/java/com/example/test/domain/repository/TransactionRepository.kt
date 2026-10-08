package com.example.test.domain.repository

import com.example.test.domain.model.Transaction
import com.example.test.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionById(id: Long): Flow<Transaction?>
    fun getTransactionsByDateRange(startDateMillis: Long, endDateMillis: Long): Flow<List<Transaction>>
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>
    fun getRecurringTransactions(): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun deleteTransactionById(id: Long)
    suspend fun deleteAllTransactions()
    fun getTotalExpenseForPeriod(startDateMillis: Long, endDateMillis: Long): Flow<Double>
    fun getTotalIncomeForPeriod(startDateMillis: Long, endDateMillis: Long): Flow<Double>
    fun getTotalExpense(): Flow<Double>
    fun getTotalIncome(): Flow<Double>
    fun getCategoryExpenseForPeriod(categoryName: String, startDateMillis: Long, endDateMillis: Long): Flow<Double>
}
