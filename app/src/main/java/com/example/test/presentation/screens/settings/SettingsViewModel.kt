package com.example.test.presentation.screens.settings

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.preferences.UserPreferences
import com.example.test.domain.model.Category
import com.example.test.domain.model.CategoryType
import com.example.test.domain.repository.BudgetRepository
import com.example.test.domain.repository.CategoryRepository
import com.example.test.domain.repository.TransactionRepository
import com.example.test.domain.usecase.ExportCsvUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(
    private val userPreferences: UserPreferences,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val exportCsvUseCase: ExportCsvUseCase = ExportCsvUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            themeMode = userPreferences.getThemeMode(),
            isBiometricEnabled = userPreferences.isBiometricEnabled()
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { currentState ->
                    currentState.copy(categories = categories)
                }
            }
        }
        viewModelScope.launch {
            userPreferences.isBiometricEnabled.collect { isEnabled ->
                _uiState.update { it.copy(isBiometricEnabled = isEnabled) }
            }
        }
    }

    fun onThemeModeChange(mode: String) {
        userPreferences.setThemeMode(mode)
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun onBiometricToggleChange(enabled: Boolean) {
        userPreferences.setBiometricEnabled(enabled)
        _uiState.update { it.copy(isBiometricEnabled = enabled) }
    }

    fun showStatusMessage(message: String) {
        _uiState.update { it.copy(exportStatusMessage = message) }
    }

    fun onOpenAddCategoryDialog() {
        _uiState.update {
            it.copy(
                showAddCategoryDialog = true,
                newCategoryName = "",
                newCategoryType = CategoryType.EXPENSE,
                newCategoryError = null
            )
        }
    }

    fun onDismissAddCategoryDialog() {
        _uiState.update { it.copy(showAddCategoryDialog = false) }
    }

    fun onNewCategoryNameChange(name: String) {
        _uiState.update {
            it.copy(
                newCategoryName = name,
                newCategoryError = null
            )
        }
    }

    fun onNewCategoryTypeChange(type: CategoryType) {
        _uiState.update { it.copy(newCategoryType = type) }
    }

    fun onSaveNewCategory() {
        val name = _uiState.value.newCategoryName.trim()
        if (name.isEmpty()) {
            _uiState.update { it.copy(newCategoryError = "Category name cannot be empty") }
            return
        }

        viewModelScope.launch {
            val category = Category(
                name = name,
                type = _uiState.value.newCategoryType,
                iconName = "Category",
                isDefault = false
            )
            categoryRepository.insertCategory(category)
            _uiState.update { it.copy(showAddCategoryDialog = false) }
        }
    }

    fun onDeleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }

    fun onOpenClearDataConfirmation() {
        _uiState.update { it.copy(showClearDataConfirmation = true) }
    }

    fun onDismissClearDataConfirmation() {
        _uiState.update { it.copy(showClearDataConfirmation = false) }
    }

    fun onConfirmClearAllData() {
        viewModelScope.launch {
            transactionRepository.deleteAllTransactions()
            budgetRepository.deleteAllBudgets()
            _uiState.update {
                it.copy(
                    showClearDataConfirmation = false,
                    exportStatusMessage = "All transaction and budget data cleared successfully."
                )
            }
        }
    }

    fun exportTransactionsCsv(context: Context) {
        viewModelScope.launch {
            val transactions = transactionRepository.getAllTransactions().firstOrNull() ?: emptyList()
            if (transactions.isEmpty()) {
                _uiState.update { it.copy(exportStatusMessage = "No transactions available to export.") }
                return@launch
            }

            val csvContent = exportCsvUseCase.generateCsvString(transactions)

            try {
                val file = File(context.cacheDir, "SpendWise_Transactions_Export.csv")
                file.writeText(csvContent)

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_SUBJECT, "SpendWise Transactions Export")
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = Intent.createChooser(shareIntent, "Export SpendWise Data via...")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)

                _uiState.update { it.copy(exportStatusMessage = "CSV exported successfully.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(exportStatusMessage = "Failed to export CSV: ${e.localizedMessage}") }
            }
        }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(exportStatusMessage = null) }
    }
}
