package com.example.test.domain.usecase

import com.example.test.domain.model.Budget
import com.example.test.domain.model.InsightType
import com.example.test.domain.model.SpendingInsight
import com.example.test.domain.model.Transaction
import com.example.test.domain.model.TransactionType
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.roundToInt

class GetInsightsUseCase {

    operator fun invoke(
        currentMonthTransactions: List<Transaction>,
        previousMonthTransactions: List<Transaction>,
        currentBudget: Budget?,
        daysInMonth: Int = 30,
        currentDayOfMonth: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    ): List<SpendingInsight> {
        val insights = mutableListOf<SpendingInsight>()

        val currentExpenses = currentMonthTransactions.filter { it.type == TransactionType.EXPENSE }
        val previousExpenses = previousMonthTransactions.filter { it.type == TransactionType.EXPENSE }

        if (currentExpenses.isEmpty()) {
            return insights
        }

        // 1. Dominant Spending Category
        val currentByCategory = currentExpenses.groupBy { it.categoryName }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val totalCurrentExpense = currentExpenses.sumOf { it.amount }

        val dominantCategory = currentByCategory.maxByOrNull { it.value }
        if (dominantCategory != null && totalCurrentExpense > 0) {
            val percentage = ((dominantCategory.value / totalCurrentExpense) * 100).roundToInt()
            if (percentage >= 30) {
                insights.add(
                    SpendingInsight(
                        id = "dominant_category",
                        title = "Top Expense Category",
                        message = "${dominantCategory.key} is your largest expense category this month, accounting for $percentage% of total spending.",
                        type = InsightType.INFO
                    )
                )
            }
        }

        // 2. Category Month-over-Month Comparison
        val previousByCategory = previousExpenses.groupBy { it.categoryName }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        for ((category, currentAmount) in currentByCategory) {
            val previousAmount = previousByCategory[category] ?: 0.0
            if (previousAmount > 0 && currentAmount > 0) {
                val changePercentage = ((currentAmount - previousAmount) / previousAmount) * 100
                val roundedChange = abs(changePercentage.roundToInt())

                if (changePercentage >= 15) {
                    insights.add(
                        SpendingInsight(
                            id = "category_increase_$category",
                            title = "Increased Spending",
                            message = "Your $category spending is $roundedChange% higher than last month.",
                            type = InsightType.WARNING,
                            percentageChange = changePercentage
                        )
                    )
                } else if (changePercentage <= -15) {
                    insights.add(
                        SpendingInsight(
                            id = "category_decrease_$category",
                            title = "Reduced Spending",
                            message = "Your $category spending is $roundedChange% lower than last month.",
                            type = InsightType.POSITIVE,
                            percentageChange = changePercentage
                        )
                    )
                }
            }
        }

        // 3. Budget Spending Pace Alert
        if (currentBudget != null && currentBudget.amount > 0) {
            val spentRatio = totalCurrentExpense / currentBudget.amount
            val daysRatio = currentDayOfMonth.toDouble() / daysInMonth.toDouble()

            if (spentRatio > 1.0) {
                val overrunAmount = totalCurrentExpense - currentBudget.amount
                insights.add(
                    SpendingInsight(
                        id = "budget_exceeded",
                        title = "Budget Exceeded",
                        message = "You have exceeded your monthly budget by ₹${String.format("%.2f", overrunAmount)}.",
                        type = InsightType.WARNING
                    )
                )
            } else if (spentRatio > daysRatio + 0.15 && currentDayOfMonth < daysInMonth) {
                val remainingDays = daysInMonth - currentDayOfMonth
                insights.add(
                    SpendingInsight(
                        id = "budget_pace_warning",
                        title = "Budget Spending Pace",
                        message = "You are spending faster than your monthly budget pace with $remainingDays days remaining.",
                        type = InsightType.WARNING
                    )
                )
            } else if (spentRatio < 0.5 && currentDayOfMonth > 20) {
                insights.add(
                    SpendingInsight(
                        id = "budget_savings_positive",
                        title = "On Track",
                        message = "Great job! You have used less than half of your budget late in the month.",
                        type = InsightType.POSITIVE
                    )
                )
            }
        }

        return insights
    }
}
