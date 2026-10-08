package com.example.test.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.test.presentation.components.BottomNavBar
import com.example.test.presentation.components.SpendWiseTopBar

@Composable
fun MainScaffold(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isTopLevelDestination = bottomNavItems.any { it.screen.route == currentRoute }

    val currentTitle = when (currentRoute) {
        Screen.Home.route -> "SpendWise"
        Screen.Transactions.route -> "Transaction History"
        Screen.Budgets.route -> "Monthly Budgets"
        Screen.Analytics.route -> "Spending Analytics"
        Screen.Settings.route -> "Settings"
        Screen.AddTransaction.route -> "Add Transaction"
        Screen.EditTransaction.route -> "Edit Transaction"
        else -> "SpendWise"
    }

    Scaffold(
        topBar = {
            SpendWiseTopBar(
                title = currentTitle,
                canNavigateBack = !isTopLevelDestination,
                onNavigateBack = { navController.popBackStack() }
            )
        },
        bottomBar = {
            if (isTopLevelDestination) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (isTopLevelDestination) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddTransaction.route) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Expense"
                    )
                }
            }
        }
    ) { innerPadding ->
        SpendWiseNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
