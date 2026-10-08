package com.example.test.domain.model

enum class CategoryType {
    EXPENSE,
    INCOME,
    BOTH
}

data class Category(
    val id: Long = 0,
    val name: String,
    val type: CategoryType,
    val iconName: String,
    val isDefault: Boolean = true
)
