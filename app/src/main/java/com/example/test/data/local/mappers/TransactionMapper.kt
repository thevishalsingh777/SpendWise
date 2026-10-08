package com.example.test.data.local.mappers

import com.example.test.data.local.entities.TransactionEntity
import com.example.test.domain.model.RecurringPeriod
import com.example.test.domain.model.Transaction
import com.example.test.domain.model.TransactionType

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        type = try { TransactionType.valueOf(type) } catch (e: Exception) { TransactionType.EXPENSE },
        categoryName = categoryName,
        dateMillis = dateMillis,
        description = description,
        note = note,
        isRecurring = isRecurring,
        recurringPeriod = recurringPeriod?.let {
            try { RecurringPeriod.valueOf(it) } catch (e: Exception) { null }
        },
        createdAtMillis = createdAtMillis
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        type = type.name,
        categoryName = categoryName,
        dateMillis = dateMillis,
        description = description,
        note = note,
        isRecurring = isRecurring,
        recurringPeriod = recurringPeriod?.name,
        createdAtMillis = createdAtMillis
    )
}
