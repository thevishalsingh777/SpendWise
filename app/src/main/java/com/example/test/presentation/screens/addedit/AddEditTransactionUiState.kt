package com.example.test.presentation.screens.addedit

import com.example.test.domain.model.Category
import com.example.test.domain.model.RecurringPeriod
import com.example.test.domain.model.TransactionType

data class AddEditTransactionUiState(
    val id: Long? = null,
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val selectedCategoryName: String = "",
    val categories: List<Category> = emptyList(),
    val dateMillis: Long = System.currentTimeMillis(),
    val description: String = "",
    val note: String = "",
    val isRecurring: Boolean = false,
    val recurringPeriod: RecurringPeriod = RecurringPeriod.MONTHLY,
    val amountError: String? = null,
    val descriptionError: String? = null,
    val categoryError: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)
