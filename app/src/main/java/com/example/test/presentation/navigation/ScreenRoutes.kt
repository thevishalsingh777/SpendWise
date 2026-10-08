package com.example.test.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Dashboard")
    object Transactions : Screen("transactions", "Transactions")
    object AddTransaction : Screen("transaction_add", "Add Transaction")
    object EditTransaction : Screen("transaction_edit/{transactionId}", "Edit Transaction") {
        fun createRoute(transactionId: Long) = "transaction_edit/$transactionId"
    }
    object Budgets : Screen("budgets", "Budgets")
    object Analytics : Screen("analytics", "Analytics")
    object Settings : Screen("settings", "Settings")
}

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Default.Home, "Home"),
    BottomNavItem(Screen.Transactions, Icons.AutoMirrored.Filled.ReceiptLong, "History"),
    BottomNavItem(Screen.Budgets, Icons.Default.AccountBalanceWallet, "Budgets"),
    BottomNavItem(Screen.Analytics, Icons.Default.BarChart, "Analytics"),
    BottomNavItem(Screen.Settings, Icons.Default.Settings, "Settings")
)
