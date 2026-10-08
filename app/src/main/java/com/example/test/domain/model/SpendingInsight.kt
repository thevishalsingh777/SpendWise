package com.example.test.domain.model

enum class InsightType {
    WARNING,
    POSITIVE,
    NEUTRAL,
    INFO
}

data class SpendingInsight(
    val id: String,
    val title: String,
    val message: String,
    val type: InsightType,
    val percentageChange: Double? = null
)
