package com.example.test.domain.usecase

import com.example.test.domain.model.Budget
import com.example.test.domain.model.FinancialSummary
import com.example.test.domain.model.Transaction
import com.example.test.domain.model.TransactionType

class CalculateSummaryUseCase {

    operator fun invoke(
        allTransactions: List<Transaction>,
        currentMonthTransactions: List<Transaction>,
        currentBudget: Budget?
    ): FinancialSummary {
        val totalIncome = allTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = allTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val totalBalance = totalIncome - totalExpense

        val currentMonthExpense = currentMonthTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val budgetLimit = currentBudget?.amount ?: 0.0

        return FinancialSummary(
            totalBalance = totalBalance,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            monthlyBudgetLimit = budgetLimit,
            monthlyBudgetSpent = currentMonthExpense
        )
    }
}
