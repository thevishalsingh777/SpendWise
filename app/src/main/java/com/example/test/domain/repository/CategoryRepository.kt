package com.example.test.domain.repository

import com.example.test.domain.model.Category
import com.example.test.domain.model.CategoryType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    fun getCategoriesByType(type: CategoryType): Flow<List<Category>>
    suspend fun insertCategory(category: Category): Long
    suspend fun deleteCategory(category: Category)
}
