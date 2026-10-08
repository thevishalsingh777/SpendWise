package com.example.test.data.local.mappers

import com.example.test.data.local.entities.BudgetEntity
import com.example.test.domain.model.Budget

fun BudgetEntity.toDomain(): Budget {
    return Budget(
        id = id,
        categoryName = categoryName,
        amount = amount,
        month = month,
        year = year
    )
}

fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = id,
        categoryName = categoryName,
        amount = amount,
        month = month,
        year = year
    )
}
