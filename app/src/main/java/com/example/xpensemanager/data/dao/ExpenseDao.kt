package com.example.xpensemanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.xpensemanager.data.entities.Expense

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY timestamp DESC")
    fun getExpensesByCategory(category: String): LiveData<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpenses(): LiveData<Double>

    @Query("SELECT SUM(amount) FROM expenses WHERE paymentMethod = 'Cash'")
    fun getTotalCashExpenses(): LiveData<Double>

    @Query("SELECT SUM(amount) FROM expenses WHERE paymentMethod = 'UPI'")
    fun getTotalUpiExpenses(): LiveData<Double>

    @Query("SELECT category, SUM(amount) as total FROM expenses GROUP BY category")
    fun getExpenseByCategory(): LiveData<List<ExpenseCategoryTotal>>
}

data class ExpenseCategoryTotal(val category: String, val total: Double)
