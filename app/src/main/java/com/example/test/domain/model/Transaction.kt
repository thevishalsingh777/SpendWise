package com.example.test.domain.model

enum class TransactionType {
    EXPENSE,
    INCOME
}

enum class RecurringPeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryName: String,
    val dateMillis: Long,
    val description: String,
    val note: String? = null,
    val isRecurring: Boolean = false,
    val recurringPeriod: RecurringPeriod? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)
