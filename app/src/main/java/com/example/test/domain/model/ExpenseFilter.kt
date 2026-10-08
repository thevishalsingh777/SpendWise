package com.example.test.domain.model

data class ExpenseFilter(
    val searchQuery: String = "",
    val type: TransactionType? = null,
    val categoryName: String? = null,
    val startDateMillis: Long? = null,
    val endDateMillis: Long? = null
)
