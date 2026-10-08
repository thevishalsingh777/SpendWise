package com.example.test

import android.app.Application
import com.example.test.data.local.database.SpendWiseDatabase
import com.example.test.data.preferences.UserPreferences
import com.example.test.data.repository.BudgetRepositoryImpl
import com.example.test.data.repository.CategoryRepositoryImpl
import com.example.test.data.repository.TransactionRepositoryImpl
import com.example.test.domain.repository.BudgetRepository
import com.example.test.domain.repository.CategoryRepository
import com.example.test.domain.repository.TransactionRepository

class SpendWiseApplication : Application() {

    val database by lazy { SpendWiseDatabase.getDatabase(this) }

    val userPreferences by lazy { UserPreferences(this) }

    val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(database.transactionDao())
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepositoryImpl(database.categoryDao())
    }

    val budgetRepository: BudgetRepository by lazy {
        BudgetRepositoryImpl(database.budgetDao())
    }
}
