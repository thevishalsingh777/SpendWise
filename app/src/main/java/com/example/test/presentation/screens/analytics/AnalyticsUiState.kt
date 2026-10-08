package com.example.test.presentation.screens.analytics

import com.example.test.presentation.components.CategorySlice

data class AnalyticsUiState(
    val monthName: String = "",
    val currentMonthTotalExpense: Double = 0.0,
    val previousMonthTotalExpense: Double = 0.0,
    val momExpenseChangePercentage: Double? = null,
    val currentMonthTotalIncome: Double = 0.0,
    val categorySlices: List<CategorySlice> = emptyList(),
    val highestSpendingCategory: String? = null,
    val highestSpendingAmount: Double = 0.0,
    val isLoading: Boolean = true
)
