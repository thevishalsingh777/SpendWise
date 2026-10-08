package com.example.test.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.test.SpendWiseApplication
import com.example.test.presentation.SpendWiseViewModelFactory
import com.example.test.presentation.components.BalanceSummaryCard
import com.example.test.presentation.components.BudgetProgressCard
import com.example.test.presentation.components.InsightCard
import com.example.test.presentation.components.TransactionItemCard
import java.util.Calendar

@Composable
fun HomeScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToTransactions: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToEditTransaction: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as SpendWiseApplication

    val viewModel: HomeViewModel = viewModel(
        factory = SpendWiseViewModelFactory(application)
    )

    val uiState by viewModel.uiState.collectAsState()

    val greeting = rememberGreeting()

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
                // Header Greeting
                Column {
                    Text(
                        text = "$greeting, ${uiState.userName}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Smart Personal Expense Tracker",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Balance Summary Card
                BalanceSummaryCard(
                    totalBalance = uiState.summary.totalBalance,
                    totalIncome = uiState.summary.totalIncome,
                    totalExpense = uiState.summary.totalExpense
                )

                // Monthly Budget Progress
                BudgetProgressCard(
                    monthlyBudgetSpent = uiState.summary.monthlyBudgetSpent,
                    monthlyBudgetLimit = uiState.summary.monthlyBudgetLimit
                )

                // Intelligent Spending Insight Card
                uiState.topInsight?.let { insight ->
                    InsightCard(
                        insight = insight,
                        onViewAnalyticsClick = onNavigateToAnalytics
                    )
                }

                // Recent Transactions Header & List
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Transactions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        if (uiState.recentTransactions.isNotEmpty()) {
                            TextButton(onClick = onNavigateToTransactions) {
                                Text("View All")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.recentTransactions.isEmpty()) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No transactions recorded yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(onClick = onNavigateToAddTransaction) {
                                    Text("Add Your First Expense")
                                }
                            }
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.recentTransactions.forEach { transaction ->
                                TransactionItemCard(
                                    transaction = transaction,
                                    onClick = { onNavigateToEditTransaction(transaction.id) }
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
private fun rememberGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 4..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
}
