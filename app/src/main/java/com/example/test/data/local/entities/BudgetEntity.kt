package com.example.test.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryName: String? = null, // null means overall total monthly budget
    val amount: Double,
    val month: Int,
    val year: Int
)
