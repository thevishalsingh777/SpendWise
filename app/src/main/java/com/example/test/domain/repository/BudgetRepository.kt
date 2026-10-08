package com.example.test.domain.repository

import com.example.test.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getAllBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>>
    fun getOverallBudgetForMonth(month: Int, year: Int): Flow<Budget?>
    fun getBudgetByCategoryForMonth(categoryName: String, month: Int, year: Int): Flow<Budget?>
    suspend fun setBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
    suspend fun deleteAllBudgets()
}
