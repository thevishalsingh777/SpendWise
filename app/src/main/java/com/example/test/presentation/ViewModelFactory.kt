package com.example.test.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.test.SpendWiseApplication
import com.example.test.presentation.screens.addedit.AddEditTransactionViewModel
import com.example.test.presentation.screens.analytics.AnalyticsViewModel
import com.example.test.presentation.screens.budgets.BudgetsViewModel
import com.example.test.presentation.screens.home.HomeViewModel
import com.example.test.presentation.screens.settings.SettingsViewModel
import com.example.test.presentation.screens.transactions.TransactionsViewModel

class SpendWiseViewModelFactory(
    private val application: SpendWiseApplication,
    private val transactionId: Long? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditTransactionViewModel::class.java)) {
            return AddEditTransactionViewModel(
                transactionRepository = application.transactionRepository,
                categoryRepository = application.categoryRepository,
                transactionId = transactionId
            ) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                transactionRepository = application.transactionRepository,
                budgetRepository = application.budgetRepository
            ) as T
        } else if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            return TransactionsViewModel(
                transactionRepository = application.transactionRepository,
                categoryRepository = application.categoryRepository
            ) as T
        } else if (modelClass.isAssignableFrom(BudgetsViewModel::class.java)) {
            return BudgetsViewModel(
                budgetRepository = application.budgetRepository,
                transactionRepository = application.transactionRepository,
                categoryRepository = application.categoryRepository
            ) as T
        } else if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
            return AnalyticsViewModel(
                transactionRepository = application.transactionRepository
            ) as T
        } else if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                userPreferences = application.userPreferences,
                categoryRepository = application.categoryRepository,
                transactionRepository = application.transactionRepository,
                budgetRepository = application.budgetRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
