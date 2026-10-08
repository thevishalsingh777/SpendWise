package com.example.test.presentation.screens.budgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.test.SpendWiseApplication
import com.example.test.presentation.SpendWiseViewModelFactory
import com.example.test.presentation.theme.financialColors
import java.text.DateFormatSymbols
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BudgetsScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as SpendWiseApplication

    val viewModel: BudgetsViewModel = viewModel(
        factory = SpendWiseViewModelFactory(application)
    )

    val uiState by viewModel.uiState.collectAsState()

    // Add / Edit Budget Dialog
    if (uiState.showAddEditDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDialog() },
            title = {
                Text(
                    text = if (uiState.editingCategoryName == null) "Set Overall Monthly Budget" else "Set Budget for ${uiState.editingCategoryName}"
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Select Budget Target:",
                        style = MaterialTheme.typography.labelMedium
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = uiState.editingCategoryName == null,
                            onClick = { viewModel.onDialogCategorySelect(null) },
                            label = { Text("Overall Budget") }
                        )

                        uiState.availableCategories.forEach { category ->
                            FilterChip(
                                selected = uiState.editingCategoryName == category.name,
                                onClick = { viewModel.onDialogCategorySelect(category.name) },
                                label = { Text(category.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = uiState.dialogAmountInput,
                        onValueChange = { viewModel.onDialogAmountChange(it) },
                        label = { Text("Budget Limit (₹)") },
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                        isError = uiState.dialogAmountError != null,
                        supportingText = uiState.dialogAmountError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onSaveBudget() }) {
                    Text("Save Budget", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    val monthName = rememberMonthName(uiState.month)

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
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Month / Year Selection Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.onMonthChange(-1) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
                        }

                        Text(
                            text = "$monthName ${uiState.year}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(onClick = { viewModel.onMonthChange(1) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month")
                        }
                    }
                }

                // Overall Monthly Budget Section
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Monthly Budget",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(onClick = { viewModel.onOpenAddEditDialog(null) }) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (uiState.overallBudget == null) "Set Budget" else "Edit")
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val limit = uiState.overallBudget?.amount ?: 0.0
                    val spent = uiState.overallSpent

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (limit > 0) {
                                val percentage = ((spent / limit) * 100).toInt()
                                val isOver = spent > limit
                                val ratio = (spent / limit).coerceIn(0.0, 1.0).toFloat()

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "₹${String.format(Locale.getDefault(), "%,.0f", spent)} / ₹${String.format(Locale.getDefault(), "%,.0f", limit)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$percentage% used",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isOver) MaterialTheme.financialColors.expense else if (percentage >= 80) MaterialTheme.financialColors.warning else MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                LinearProgressIndicator(
                                    progress = { ratio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp),
                                    color = if (isOver) MaterialTheme.financialColors.expense else if (ratio >= 0.8f) MaterialTheme.financialColors.warning else MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    strokeCap = StrokeCap.Round
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                val remaining = limit - spent
                                Text(
                                    text = if (remaining >= 0) "Remaining Budget: ₹${String.format(Locale.getDefault(), "%,.0f", remaining)}" else "Over Budget by ₹${String.format(Locale.getDefault(), "%,.0f", -remaining)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (remaining < 0) MaterialTheme.financialColors.expense else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "No overall monthly budget configured for $monthName.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { viewModel.onOpenAddEditDialog(null) }) {
                                    Text("Set Overall Monthly Budget")
                                }
                            }
                        }
                    }
                }

                // Category Specific Budgets Section
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Category Budgets",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(onClick = {
                            val firstCategoryName = uiState.availableCategories.firstOrNull()?.name
                            viewModel.onOpenAddEditDialog(firstCategoryName)
                        }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Add Category Budget")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.categoryBudgets.isEmpty()) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No category-specific budgets set for this month.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(onClick = {
                                    val firstCategoryName = uiState.availableCategories.firstOrNull()?.name
                                    viewModel.onOpenAddEditDialog(firstCategoryName)
                                }) {
                                    Text("Set Category Budget")
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.categoryBudgets.forEach { catProgress ->
                                CategoryBudgetCard(
                                    catProgress = catProgress,
                                    onEditClick = { viewModel.onOpenAddEditDialog(catProgress.categoryName) },
                                    onDeleteClick = { viewModel.onDeleteBudget(catProgress.categoryName) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryBudgetCard(
    catProgress: CategoryBudgetProgress,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isOver = catProgress.amountSpent > catProgress.budgetLimit
    val ratio = catProgress.progressRatio

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = catProgress.categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Category Budget")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Category Budget", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "₹${String.format(Locale.getDefault(), "%,.0f", catProgress.amountSpent)} / ₹${String.format(Locale.getDefault(), "%,.0f", catProgress.budgetLimit)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${catProgress.spentPercentage}% used",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOver) MaterialTheme.financialColors.expense else if (ratio >= 0.8f) MaterialTheme.financialColors.warning else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { ratio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (isOver) MaterialTheme.financialColors.expense else if (ratio >= 0.8f) MaterialTheme.financialColors.warning else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun rememberMonthName(month: Int): String {
    return DateFormatSymbols().months[month - 1]
}
