package com.example.test.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.domain.model.Transaction
import com.example.test.domain.model.TransactionType
import com.example.test.domain.repository.CategoryRepository
import com.example.test.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionsViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val transactionsFlow = transactionRepository.getAllTransactions()
            val categoriesFlow = categoryRepository.getAllCategories()

            combine(transactionsFlow, categoriesFlow) { transactions, categories ->
                val filtered = applyFilters(
                    transactions = transactions,
                    query = _uiState.value.searchQuery,
                    type = _uiState.value.selectedType,
                    categoryName = _uiState.value.selectedCategoryName
                )

                val grouped = groupTransactionsByDate(filtered)

                _uiState.value.copy(
                    transactions = filtered,
                    groupedTransactions = grouped,
                    categories = categories,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { currentState ->
            currentState.copy(searchQuery = query)
        }
        reapplyFilters()
    }

    fun onTypeFilterSelect(type: TransactionType?) {
        _uiState.update { currentState ->
            currentState.copy(selectedType = type)
        }
        reapplyFilters()
    }

    fun onCategoryFilterSelect(categoryName: String?) {
        _uiState.update { currentState ->
            currentState.copy(selectedCategoryName = categoryName)
        }
        reapplyFilters()
    }

    fun onDeleteClick(transaction: Transaction) {
        _uiState.update {
            it.copy(transactionToDelete = transaction)
        }
    }

    fun onDismissDeleteDialog() {
        _uiState.update {
            it.copy(transactionToDelete = null)
        }
    }

    fun onConfirmDelete() {
        val target = _uiState.value.transactionToDelete
        if (target != null) {
            viewModelScope.launch {
                transactionRepository.deleteTransaction(target)
                _uiState.update { it.copy(transactionToDelete = null) }
            }
        }
    }

    private fun reapplyFilters() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions().collect { all ->
                val filtered = applyFilters(
                    transactions = all,
                    query = _uiState.value.searchQuery,
                    type = _uiState.value.selectedType,
                    categoryName = _uiState.value.selectedCategoryName
                )
                val grouped = groupTransactionsByDate(filtered)
                _uiState.update {
                    it.copy(
                        transactions = filtered,
                        groupedTransactions = grouped
                    )
                }
            }
        }
    }

    private fun applyFilters(
        transactions: List<Transaction>,
        query: String,
        type: TransactionType?,
        categoryName: String?
    ): List<Transaction> {
        return transactions.filter { t ->
            val matchesQuery = query.isBlank() ||
                    t.description.contains(query, ignoreCase = true) ||
                    t.categoryName.contains(query, ignoreCase = true) ||
                    (t.note != null && t.note.contains(query, ignoreCase = true))

            val matchesType = type == null || t.type == type
            val matchesCategory = categoryName.isNullOrBlank() || t.categoryName.equals(categoryName, ignoreCase = true)

            matchesQuery && matchesType && matchesCategory
        }
    }

    private fun groupTransactionsByDate(transactions: List<Transaction>): Map<String, List<Transaction>> {
        val todayCal = Calendar.getInstance()
        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        return transactions.groupBy { t ->
            val itemCal = Calendar.getInstance().apply { timeInMillis = t.dateMillis }
            when {
                isSameDay(itemCal, todayCal) -> "Today"
                isSameDay(itemCal, yesterdayCal) -> "Yesterday"
                else -> dateFormat.format(Date(t.dateMillis))
            }
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
