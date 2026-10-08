package com.example.test.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.test.data.local.dao.BudgetDao
import com.example.test.data.local.dao.CategoryDao
import com.example.test.data.local.dao.TransactionDao
import com.example.test.data.local.entities.BudgetEntity
import com.example.test.data.local.entities.CategoryEntity
import com.example.test.data.local.entities.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SpendWiseDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: SpendWiseDatabase? = null

        fun getDatabase(context: Context): SpendWiseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpendWiseDatabase::class.java,
                    "spendwise_database"
                )
                    .addCallback(SpendWiseDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SpendWiseDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDefaultCategories(database.categoryDao())
                }
            }
        }

        private suspend fun populateDefaultCategories(categoryDao: CategoryDao) {
            val defaultCategories = listOf(
                // Expense Categories
                CategoryEntity(name = "Food", type = "EXPENSE", iconName = "Restaurant", isDefault = true),
                CategoryEntity(name = "Transportation", type = "EXPENSE", iconName = "DirectionsCar", isDefault = true),
                CategoryEntity(name = "Shopping", type = "EXPENSE", iconName = "ShoppingBag", isDefault = true),
                CategoryEntity(name = "Entertainment", type = "EXPENSE", iconName = "Movie", isDefault = true),
                CategoryEntity(name = "Education", type = "EXPENSE", iconName = "School", isDefault = true),
                CategoryEntity(name = "Bills", type = "EXPENSE", iconName = "Receipt", isDefault = true),
                CategoryEntity(name = "Healthcare", type = "EXPENSE", iconName = "MedicalServices", isDefault = true),
                CategoryEntity(name = "Travel", type = "EXPENSE", iconName = "Flight", isDefault = true),
                CategoryEntity(name = "Personal", type = "EXPENSE", iconName = "Person", isDefault = true),
                CategoryEntity(name = "Other Expense", type = "EXPENSE", iconName = "MoreHoriz", isDefault = true),

                // Income Categories
                CategoryEntity(name = "Salary", type = "INCOME", iconName = "Work", isDefault = true),
                CategoryEntity(name = "Freelance", type = "INCOME", iconName = "Computer", isDefault = true),
                CategoryEntity(name = "Scholarship", type = "INCOME", iconName = "School", isDefault = true),
                CategoryEntity(name = "Gift", type = "INCOME", iconName = "CardGiftcard", isDefault = true),
                CategoryEntity(name = "Other Income", type = "INCOME", iconName = "MoreHoriz", isDefault = true)
            )
            categoryDao.insertCategories(defaultCategories)
        }
    }
}
