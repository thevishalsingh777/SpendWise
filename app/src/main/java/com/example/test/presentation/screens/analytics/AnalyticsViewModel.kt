package com.example.test.presentation.screens.analytics

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.domain.model.TransactionType
import com.example.test.domain.repository.TransactionRepository
import com.example.test.presentation.components.CategorySlice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.util.Calendar

class AnalyticsViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    // Professional, restrained color palette for category slices
    private val chartColors = listOf(
        Color(0xFF455A64), // Blue Gray
        Color(0xFF2E7D32), // Emerald Green
        Color(0xFFC62828), // Crimson Red
        Color(0xFFE65100), // Muted Orange
        Color(0xFF1565C0), // Royal Blue
        Color(0xFF6A1B9A), // Deep Purple
        Color(0xFF00838F), // Cyan
        Color(0xFF4E342E), // Brown
        Color(0xFF37474F)  // Charcoal
    )

    init {
        loadAnalyticsData()
    }

    private fun loadAnalyticsData() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)
            val monthName = DateFormatSymbols().months[currentMonth]

            val currentMonthStart = calendar.apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val currentMonthEnd = calendar.apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val prevCal = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
            val prevMonthStart = prevCal.apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val prevMonthEnd = prevCal.apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            transactionRepository.getAllTransactions().collect { allTransactions ->
                val currentMonthTx = allTransactions.filter { it.dateMillis in currentMonthStart..currentMonthEnd }
                val prevMonthTx = allTransactions.filter { it.dateMillis in prevMonthStart..prevMonthEnd }

                val currentExpenses = currentMonthTx.filter { it.type == TransactionType.EXPENSE }
                val currentIncomes = currentMonthTx.filter { it.type == TransactionType.INCOME }
                val prevExpenses = prevMonthTx.filter { it.type == TransactionType.EXPENSE }

                val currentTotalExpense = currentExpenses.sumOf { it.amount }
                val currentTotalIncome = currentIncomes.sumOf { it.amount }
                val prevTotalExpense = prevExpenses.sumOf { it.amount }

                val momChange = if (prevTotalExpense > 0) {
                    ((currentTotalExpense - prevTotalExpense) / prevTotalExpense) * 100
                } else null

                // Category Breakdown
                val categoryGroups = currentExpenses.groupBy { it.categoryName }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }
                    .toList()
                    .sortedByDescending { it.second }

                val topCategory = categoryGroups.firstOrNull()

                val slices = categoryGroups.mapIndexed { index, (catName, amt) ->
                    val ratio = if (currentTotalExpense > 0) (amt / currentTotalExpense).toFloat() else 0f
                    CategorySlice(
                        categoryName = catName,
                        amount = amt,
                        percentage = ratio,
                        color = chartColors[index % chartColors.size]
                    )
                }

                _uiState.value = AnalyticsUiState(
                    monthName = monthName,
                    currentMonthTotalExpense = currentTotalExpense,
                    previousMonthTotalExpense = prevTotalExpense,
                    momExpenseChangePercentage = momChange,
                    currentMonthTotalIncome = currentTotalIncome,
                    categorySlices = slices,
                    highestSpendingCategory = topCategory?.first,
                    highestSpendingAmount = topCategory?.second ?: 0.0,
                    isLoading = false
                )
            }
        }
    }
}
