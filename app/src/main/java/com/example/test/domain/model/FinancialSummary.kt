package com.example.test.domain.model

data class FinancialSummary(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val monthlyBudgetLimit: Double = 0.0,
    val monthlyBudgetSpent: Double = 0.0
) {
    val budgetRemaining: Double
        get() = (monthlyBudgetLimit - monthlyBudgetSpent).coerceAtLeast(0.0)

    val budgetProgressRatio: Float
        get() = if (monthlyBudgetLimit > 0) {
            (monthlyBudgetSpent / monthlyBudgetLimit).coerceIn(0.0, 1.0).toFloat()
        } else 0f
}
