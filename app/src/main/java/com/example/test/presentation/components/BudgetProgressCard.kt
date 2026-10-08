package com.example.test.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.test.presentation.theme.financialColors
import java.util.Locale

@Composable
fun BudgetProgressCard(
    monthlyBudgetSpent: Double,
    monthlyBudgetLimit: Double,
    modifier: Modifier = Modifier,
    onConfigureBudgetClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monthly Budget",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (monthlyBudgetLimit > 0) {
                    val spentPercentage = ((monthlyBudgetSpent / monthlyBudgetLimit) * 100).toInt()
                    Text(
                        text = "$spentPercentage% used",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (spentPercentage >= 100) {
                            MaterialTheme.financialColors.expense
                        } else if (spentPercentage >= 80) {
                            MaterialTheme.financialColors.warning
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (monthlyBudgetLimit > 0) {
                val progressRatio = (monthlyBudgetSpent / monthlyBudgetLimit).coerceIn(0.0, 1.0).toFloat()
                val isOverBudget = monthlyBudgetSpent > monthlyBudgetLimit

                Text(
                    text = "₹${String.format(Locale.getDefault(), "%,.0f", monthlyBudgetSpent)} / ₹${String.format(Locale.getDefault(), "%,.0f", monthlyBudgetLimit)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (isOverBudget) {
                        MaterialTheme.financialColors.expense
                    } else if (progressRatio >= 0.8f) {
                        MaterialTheme.financialColors.warning
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(8.dp))

                val remainingAmount = monthlyBudgetLimit - monthlyBudgetSpent
                val remainingText = if (remainingAmount >= 0) {
                    "Remaining: ₹${String.format(Locale.getDefault(), "%,.0f", remainingAmount)}"
                } else {
                    "Over budget by ₹${String.format(Locale.getDefault(), "%,.0f", -remainingAmount)}"
                }

                Text(
                    text = remainingText,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (remainingAmount < 0) MaterialTheme.financialColors.expense else MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "No budget configured for this month.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
