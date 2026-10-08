package com.example.test.domain.usecase

import com.example.test.domain.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportCsvUseCase {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun generateCsvString(transactions: List<Transaction>): String {
        val builder = StringBuilder()
        builder.append("ID,Date,Type,Category,Amount,Description,Note,IsRecurring,RecurringPeriod\n")

        for (t in transactions) {
            val formattedDate = dateFormat.format(Date(t.dateMillis))
            val safeDescription = escapeCsv(t.description)
            val safeNote = escapeCsv(t.note ?: "")
            val safeCategory = escapeCsv(t.categoryName)

            builder.append("${t.id},")
                .append("\"$formattedDate\",")
                .append("${t.type.name},")
                .append("\"$safeCategory\",")
                .append("${t.amount},")
                .append("\"$safeDescription\",")
                .append("\"$safeNote\",")
                .append("${t.isRecurring},")
                .append("${t.recurringPeriod?.name ?: ""}\n")
        }

        return builder.toString()
    }

    private fun escapeCsv(value: String): String {
        return value.replace("\"", "\"\"")
    }
}
