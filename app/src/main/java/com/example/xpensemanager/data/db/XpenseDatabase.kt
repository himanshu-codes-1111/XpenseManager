package com.example.xpensemanager.data.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.xpensemanager.data.dao.CategoryDao
import com.example.xpensemanager.data.dao.ExpenseDao
import com.example.xpensemanager.data.entities.Category
import com.example.xpensemanager.data.entities.Expense

@Database(entities = [Expense::class, Category::class], version = 2, exportSchema = false)
abstract class XpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile private var INSTANCE: XpenseDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS expenses_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        amount REAL NOT NULL,
                        category TEXT NOT NULL,
                        description TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        paymentMethod TEXT NOT NULL,
                        FOREIGN KEY (category) REFERENCES categories(name) ON DELETE CASCADE
                    )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO expenses_new (id, amount, category, description, timestamp, paymentMethod)
                    SELECT id, amount, category, description, timestamp, paymentMethod FROM expenses
                """.trimIndent())

                database.execSQL("DROP TABLE expenses")

                database.execSQL("ALTER TABLE expenses_new RENAME TO expenses")

                database.execSQL("CREATE INDEX index_expenses_category ON expenses(category)")

                database.execSQL("CREATE UNIQUE INDEX index_categories_name ON categories(name)")
            }
        }

        fun getDatabase(context: Context): XpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    XpenseDatabase::class.java,
                    "xpense_manager_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
