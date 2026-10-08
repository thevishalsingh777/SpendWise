package com.example.test.domain.model

data class Budget(
    val id: Long = 0,
    val categoryName: String? = null, // null indicates total monthly budget
    val amount: Double,
    val month: Int,
    val year: Int
)
