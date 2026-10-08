package com.example.test.data.local.mappers

import com.example.test.data.local.entities.CategoryEntity
import com.example.test.domain.model.Category
import com.example.test.domain.model.CategoryType

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        type = try { CategoryType.valueOf(type) } catch (e: Exception) { CategoryType.EXPENSE },
        iconName = iconName,
        isDefault = isDefault
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        type = type.name,
        iconName = iconName,
        isDefault = isDefault
    )
}
