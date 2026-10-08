package com.example.test.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.test.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC, id DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionById(id: Long): Flow<TransactionEntity?>

    @Query("SELECT * FROM transactions WHERE dateMillis >= :startDateMillis AND dateMillis <= :endDateMillis ORDER BY dateMillis DESC")
    fun getTransactionsByDateRange(startDateMillis: Long, endDateMillis: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY dateMillis DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE isRecurring = 1 ORDER BY dateMillis DESC")
    fun getRecurringTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND dateMillis >= :startDateMillis AND dateMillis <= :endDateMillis")
    fun getTotalExpenseForPeriod(startDateMillis: Long, endDateMillis: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND dateMillis >= :startDateMillis AND dateMillis <= :endDateMillis")
    fun getTotalIncomeForPeriod(startDateMillis: Long, endDateMillis: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND categoryName = :categoryName AND dateMillis >= :startDateMillis AND dateMillis <= :endDateMillis")
    fun getCategoryExpenseForPeriod(categoryName: String, startDateMillis: Long, endDateMillis: Long): Flow<Double?>
}
