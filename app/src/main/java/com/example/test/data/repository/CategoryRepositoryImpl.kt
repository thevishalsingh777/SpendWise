package com.example.test.data.repository

import com.example.test.data.local.dao.CategoryDao
import com.example.test.data.local.mappers.toDomain
import com.example.test.data.local.mappers.toEntity
import com.example.test.domain.model.Category
import com.example.test.domain.model.CategoryType
import com.example.test.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getCategoriesByType(type: CategoryType): Flow<List<Category>> {
        return categoryDao.getCategoriesByType(type.name).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }
}
