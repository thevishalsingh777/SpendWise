package com.example.test.presentation.screens.home

import com.example.test.domain.model.FinancialSummary
import com.example.test.domain.model.SpendingInsight
import com.example.test.domain.model.Transaction

data class HomeUiState(
    val userName: String = "Vishal",
    val summary: FinancialSummary = FinancialSummary(),
    val recentTransactions: List<Transaction> = emptyList(),
    val topInsight: SpendingInsight? = null,
    val isLoading: Boolean = true
)
