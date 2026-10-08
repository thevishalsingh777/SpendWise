package com.example.test.data.repository

import com.example.test.data.local.dao.BudgetDao
import com.example.test.data.local.mappers.toDomain
import com.example.test.data.local.mappers.toEntity
import com.example.test.domain.model.Budget
import com.example.test.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepositoryImpl(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getAllBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>> {
        return budgetDao.getAllBudgetsForMonth(month, year).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getOverallBudgetForMonth(month: Int, year: Int): Flow<Budget?> {
        return budgetDao.getOverallBudgetForMonth(month, year).map { it?.toDomain() }
    }

    override fun getBudgetByCategoryForMonth(categoryName: String, month: Int, year: Int): Flow<Budget?> {
        return budgetDao.getBudgetByCategoryForMonth(categoryName, month, year).map { it?.toDomain() }
    }

    override suspend fun setBudget(budget: Budget) {
        budgetDao.insertOrUpdateBudget(budget.toEntity())
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget.toEntity())
    }

    override suspend fun deleteAllBudgets() {
        budgetDao.deleteAllBudgets()
    }
}
