package com.example.test.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.domain.model.Budget
import com.example.test.domain.model.Transaction
import com.example.test.domain.repository.BudgetRepository
import com.example.test.domain.repository.TransactionRepository
import com.example.test.domain.usecase.CalculateSummaryUseCase
import com.example.test.domain.usecase.GetInsightsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val calculateSummaryUseCase: CalculateSummaryUseCase = CalculateSummaryUseCase(),
    private val getInsightsUseCase: GetInsightsUseCase = GetInsightsUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentYear = calendar.get(Calendar.YEAR)

            // Current month start & end millis
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

            // Previous month start & end millis
            val prevCal = Calendar.getInstance().apply {
                add(Calendar.MONTH, -1)
            }
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

            val allTransactionsFlow = transactionRepository.getAllTransactions()
            val currentBudgetFlow = budgetRepository.getOverallBudgetForMonth(currentMonth, currentYear)

            combine(allTransactionsFlow, currentBudgetFlow) { allTransactions, currentBudget ->
                val currentMonthTransactions = allTransactions.filter {
                    it.dateMillis in currentMonthStart..currentMonthEnd
                }
                val prevMonthTransactions = allTransactions.filter {
                    it.dateMillis in prevMonthStart..prevMonthEnd
                }

                val summary = calculateSummaryUseCase(
                    allTransactions = allTransactions,
                    currentMonthTransactions = currentMonthTransactions,
                    currentBudget = currentBudget
                )

                val insights = getInsightsUseCase(
                    currentMonthTransactions = currentMonthTransactions,
                    previousMonthTransactions = prevMonthTransactions,
                    currentBudget = currentBudget
                )

                val recent = allTransactions.take(5)

                HomeUiState(
                    userName = "Vishal",
                    summary = summary,
                    recentTransactions = recent,
                    topInsight = insights.firstOrNull(),
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}
