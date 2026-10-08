package com.example.test.presentation.screens.settings

import com.example.test.domain.model.Category
import com.example.test.domain.model.CategoryType

data class SettingsUiState(
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val currencySymbol: String = "₹",
    val categories: List<Category> = emptyList(),
    val showAddCategoryDialog: Boolean = false,
    val newCategoryName: String = "",
    val newCategoryType: CategoryType = CategoryType.EXPENSE,
    val newCategoryError: String? = null,
    val showClearDataConfirmation: Boolean = false,
    val exportStatusMessage: String? = null,
    val isLoading: Boolean = false
)
