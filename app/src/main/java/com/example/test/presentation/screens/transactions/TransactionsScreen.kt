package com.example.test.presentation.screens.transactions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.test.SpendWiseApplication
import com.example.test.domain.model.Transaction
import com.example.test.domain.model.TransactionType
import com.example.test.presentation.SpendWiseViewModelFactory
import com.example.test.presentation.components.TransactionItemCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionsScreen(
    onNavigateToEditTransaction: (Long) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as SpendWiseApplication

    val viewModel: TransactionsViewModel = viewModel(
        factory = SpendWiseViewModelFactory(application)
    )

    val uiState by viewModel.uiState.collectAsState()

    var selectedTransactionForActions by remember { mutableStateOf<Transaction?>(null) }

    // Delete Confirmation Dialog
    if (uiState.transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDeleteDialog() },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onConfirmDelete() }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissDeleteDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Transaction Actions Sheet / Dialog (Edit or Delete)
    if (selectedTransactionForActions != null) {
        val target = selectedTransactionForActions!!
        AlertDialog(
            onDismissRequest = { selectedTransactionForActions = null },
            title = { Text(target.description) },
            text = { Text("Choose an action for this transaction.") },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        selectedTransactionForActions = null
                        onNavigateToEditTransaction(target.id)
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Edit")
                    }

                    TextButton(onClick = {
                        selectedTransactionForActions = null
                        viewModel.onDeleteClick(target)
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTransactionForActions = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search & Filter Header Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Search title, category, or note...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Type Filter Chips Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.selectedType == null,
                                onClick = { viewModel.onTypeFilterSelect(null) },
                                label = { Text("All Types") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.selectedType == TransactionType.EXPENSE,
                                onClick = { viewModel.onTypeFilterSelect(TransactionType.EXPENSE) },
                                label = { Text("Expense Only") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.selectedType == TransactionType.INCOME,
                                onClick = { viewModel.onTypeFilterSelect(TransactionType.INCOME) },
                                label = { Text("Income Only") }
                            )
                        }
                    }

                    // Category Filter Chips Row
                    if (uiState.categories.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = uiState.selectedCategoryName == null,
                                    onClick = { viewModel.onCategoryFilterSelect(null) },
                                    label = { Text("All Categories") }
                                )
                            }
                            items(uiState.categories) { category ->
                                val isSelected = uiState.selectedCategoryName == category.name
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) viewModel.onCategoryFilterSelect(null)
                                        else viewModel.onCategoryFilterSelect(category.name)
                                    },
                                    label = { Text(category.name) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }
                }

                // Transactions List Grouped by Date
                if (uiState.groupedTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions found matching your search or filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        uiState.groupedTransactions.forEach { (dateGroupHeader, itemsInGroup) ->
                            stickyHeader {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = dateGroupHeader,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            items(itemsInGroup) { transaction ->
                                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                                    TransactionItemCard(
                                        transaction = transaction,
                                        onClick = { selectedTransactionForActions = transaction }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
