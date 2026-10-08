package com.example.test.presentation.screens.transactions

import com.example.test.domain.model.Category
import com.example.test.domain.model.Transaction
import com.example.test.domain.model.TransactionType

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val groupedTransactions: Map<String, List<Transaction>> = emptyMap(),
    val searchQuery: String = "",
    val selectedType: TransactionType? = null, // null = All
    val selectedCategoryName: String? = null, // null = All
    val categories: List<Category> = emptyList(),
    val transactionToDelete: Transaction? = null,
    val isLoading: Boolean = true
)
