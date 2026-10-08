package com.example.test.presentation.screens.budgets

import com.example.test.domain.model.Budget
import com.example.test.domain.model.Category
import java.util.Calendar

data class CategoryBudgetProgress(
    val categoryName: String,
    val budgetLimit: Double,
    val amountSpent: Double
) {
    val progressRatio: Float
        get() = if (budgetLimit > 0) (amountSpent / budgetLimit).coerceIn(0.0, 1.0).toFloat() else 0f

    val remainingAmount: Double
        get() = budgetLimit - amountSpent

    val spentPercentage: Int
        get() = if (budgetLimit > 0) ((amountSpent / budgetLimit) * 100).toInt() else 0
}

data class BudgetsUiState(
    val month: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val year: Int = Calendar.getInstance().get(Calendar.YEAR),
    val overallBudget: Budget? = null,
    val overallSpent: Double = 0.0,
    val categoryBudgets: List<CategoryBudgetProgress> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
    val showAddEditDialog: Boolean = false,
    val editingCategoryName: String? = null, // null = Overall monthly budget
    val dialogAmountInput: String = "",
    val dialogAmountError: String? = null,
    val isLoading: Boolean = true
)
