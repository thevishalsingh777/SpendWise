package com.example.test.presentation.screens.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.domain.model.Budget
import com.example.test.domain.model.CategoryType
import com.example.test.domain.model.TransactionType
import com.example.test.domain.repository.BudgetRepository
import com.example.test.domain.repository.CategoryRepository
import com.example.test.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class BudgetsViewModel(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetsUiState())
    val uiState: StateFlow<BudgetsUiState> = _uiState.asStateFlow()

    init {
        loadBudgetsData()
    }

    private fun loadBudgetsData() {
        viewModelScope.launch {
            val m = _uiState.value.month
            val y = _uiState.value.year

            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, y)
                set(Calendar.MONTH, m - 1)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startMillis = calendar.timeInMillis

            val endMillis = calendar.apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val budgetsFlow = budgetRepository.getAllBudgetsForMonth(m, y)
            val transactionsFlow = transactionRepository.getTransactionsByDateRange(startMillis, endMillis)
            val categoriesFlow = categoryRepository.getAllCategories()

            combine(budgetsFlow, transactionsFlow, categoriesFlow) { budgets, transactions, categories ->
                val overallBudget = budgets.find { it.categoryName == null }
                val categoryBudgetsList = budgets.filter { it.categoryName != null }

                val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
                val overallSpent = expenseTransactions.sumOf { it.amount }

                val spentByCategory = expenseTransactions.groupBy { it.categoryName }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }

                val progressList = categoryBudgetsList.map { b ->
                    val catName = b.categoryName ?: ""
                    CategoryBudgetProgress(
                        categoryName = catName,
                        budgetLimit = b.amount,
                        amountSpent = spentByCategory[catName] ?: 0.0
                    )
                }

                _uiState.value.copy(
                    overallBudget = overallBudget,
                    overallSpent = overallSpent,
                    categoryBudgets = progressList,
                    availableCategories = categories.filter { it.type == CategoryType.EXPENSE || it.type == CategoryType.BOTH },
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onMonthChange(delta: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, _uiState.value.year)
            set(Calendar.MONTH, _uiState.value.month - 1)
            add(Calendar.MONTH, delta)
        }
        _uiState.update {
            it.copy(
                month = calendar.get(Calendar.MONTH) + 1,
                year = calendar.get(Calendar.YEAR),
                isLoading = true
            )
        }
        loadBudgetsData()
    }

    fun onOpenAddEditDialog(categoryName: String? = null) {
        viewModelScope.launch {
            val currentBudgets = budgetRepository.getAllBudgetsForMonth(_uiState.value.month, _uiState.value.year).firstOrNull() ?: emptyList()
            val existingBudget = currentBudgets.find { it.categoryName == categoryName }

            _uiState.update {
                it.copy(
                    showAddEditDialog = true,
                    editingCategoryName = categoryName,
                    dialogAmountInput = existingBudget?.let { b ->
                        if (b.amount % 1.0 == 0.0) b.amount.toLong().toString() else b.amount.toString()
                    } ?: "",
                    dialogAmountError = null
                )
            }
        }
    }

    fun onDismissDialog() {
        _uiState.update {
            it.copy(showAddEditDialog = false)
        }
    }

    fun onDialogAmountChange(input: String) {
        val filtered = input.filter { it.isDigit() || it == '.' }
        _uiState.update {
            it.copy(
                dialogAmountInput = filtered,
                dialogAmountError = null
            )
        }
    }

    fun onDialogCategorySelect(categoryName: String?) {
        _uiState.update {
            it.copy(editingCategoryName = categoryName)
        }
    }

    fun onSaveBudget() {
        val currentState = _uiState.value
        val amountParsed = currentState.dialogAmountInput.toDoubleOrNull()

        if (amountParsed == null || amountParsed <= 0.0) {
            _uiState.update {
                it.copy(dialogAmountError = "Please enter a valid budget amount > ₹0")
            }
            return
        }

        viewModelScope.launch {
            val budget = Budget(
                categoryName = currentState.editingCategoryName,
                amount = amountParsed,
                month = currentState.month,
                year = currentState.year
            )
            budgetRepository.setBudget(budget)
            _uiState.update {
                it.copy(showAddEditDialog = false)
            }
        }
    }

    fun onDeleteBudget(categoryName: String?) {
        viewModelScope.launch {
            val currentBudgets = budgetRepository.getAllBudgetsForMonth(_uiState.value.month, _uiState.value.year).firstOrNull() ?: emptyList()
            val target = currentBudgets.find { it.categoryName == categoryName }
            if (target != null) {
                budgetRepository.deleteBudget(target)
            }
        }
    }
}
