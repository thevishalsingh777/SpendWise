package com.example.test.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String, // "EXPENSE" or "INCOME"
    val categoryName: String,
    val dateMillis: Long,
    val description: String,
    val note: String? = null,
    val isRecurring: Boolean = false,
    val recurringPeriod: String? = null, // "DAILY", "WEEKLY", "MONTHLY", "YEARLY"
    val createdAtMillis: Long = System.currentTimeMillis()
)
