package com.example.test.presentation.screens.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.domain.model.CategoryType
import com.example.test.domain.model.RecurringPeriod
import com.example.test.domain.model.Transaction
import com.example.test.domain.model.TransactionType
import com.example.test.domain.repository.CategoryRepository
import com.example.test.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddEditTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionId: Long? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditTransactionUiState(id = transactionId))
    val uiState: StateFlow<AddEditTransactionUiState> = _uiState.asStateFlow()

    init {
        observeCategories()
        if (transactionId != null && transactionId > 0) {
            loadExistingTransaction(transactionId)
        }
    }

    private fun observeCategories() {
        viewModelScope.launch {
            val typeFilter = if (_uiState.value.type == TransactionType.EXPENSE) CategoryType.EXPENSE else CategoryType.INCOME
            categoryRepository.getCategoriesByType(typeFilter).collect { categories ->
                _uiState.update { currentState ->
                    val defaultCat = if (currentState.selectedCategoryName.isEmpty() && categories.isNotEmpty()) {
                        categories.first().name
                    } else {
                        currentState.selectedCategoryName
                    }
                    currentState.copy(
                        categories = categories,
                        selectedCategoryName = defaultCat
                    )
                }
            }
        }
    }

    private fun loadExistingTransaction(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val transaction = transactionRepository.getTransactionById(id).firstOrNull()
            if (transaction != null) {
                _uiState.update {
                    it.copy(
                        id = transaction.id,
                        amount = if (transaction.amount % 1.0 == 0.0) {
                            transaction.amount.toLong().toString()
                        } else {
                            transaction.amount.toString()
                        },
                        type = transaction.type,
                        selectedCategoryName = transaction.categoryName,
                        dateMillis = transaction.dateMillis,
                        description = transaction.description,
                        note = transaction.note ?: "",
                        isRecurring = transaction.isRecurring,
                        recurringPeriod = transaction.recurringPeriod ?: RecurringPeriod.MONTHLY,
                        isLoading = false
                    )
                }
                observeCategories()
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onTypeChange(newType: TransactionType) {
        if (_uiState.value.type != newType) {
            _uiState.update {
                it.copy(
                    type = newType,
                    selectedCategoryName = "" // Reset selected category when type changes
                )
            }
            observeCategories()
        }
    }

    fun onAmountChange(amountStr: String) {
        val filtered = amountStr.filter { it.isDigit() || it == '.' }
        _uiState.update {
            it.copy(
                amount = filtered,
                amountError = null
            )
        }
    }

    fun onCategorySelect(categoryName: String) {
        _uiState.update {
            it.copy(
                selectedCategoryName = categoryName,
                categoryError = null
            )
        }
    }

    fun onDateChange(dateMillis: Long) {
        _uiState.update {
            it.copy(dateMillis = dateMillis)
        }
    }

    fun onDescriptionChange(desc: String) {
        _uiState.update {
            it.copy(
                description = desc,
                descriptionError = null
            )
        }
    }

    fun onNoteChange(note: String) {
        _uiState.update {
            it.copy(note = note)
        }
    }

    fun onRecurringToggle(isRecurring: Boolean) {
        _uiState.update {
            it.copy(isRecurring = isRecurring)
        }
    }

    fun onRecurringPeriodChange(period: RecurringPeriod) {
        _uiState.update {
            it.copy(recurringPeriod = period)
        }
    }

    fun saveTransaction() {
        val currentState = _uiState.value
        val parsedAmount = currentState.amount.toDoubleOrNull()

        var hasError = false
        var amountErr: String? = null
        var descErr: String? = null
        var catErr: String? = null

        if (parsedAmount == null || parsedAmount <= 0.0) {
            amountErr = "Please enter a valid amount greater than ₹0"
            hasError = true
        }

        if (currentState.description.trim().isEmpty()) {
            descErr = "Please enter a transaction title or description"
            hasError = true
        }

        if (currentState.selectedCategoryName.trim().isEmpty()) {
            catErr = "Please select a category"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    amountError = amountErr,
                    descriptionError = descErr,
                    categoryError = catErr
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val transaction = Transaction(
                id = currentState.id ?: 0,
                amount = parsedAmount!!,
                type = currentState.type,
                categoryName = currentState.selectedCategoryName,
                dateMillis = currentState.dateMillis,
                description = currentState.description.trim(),
                note = currentState.note.trim().ifEmpty { null },
                isRecurring = currentState.isRecurring,
                recurringPeriod = if (currentState.isRecurring) currentState.recurringPeriod else null
            )

            if (currentState.id != null && currentState.id > 0) {
                transactionRepository.updateTransaction(transaction)
            } else {
                transactionRepository.insertTransaction(transaction)
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSaved = true
                )
            }
        }
    }
}
