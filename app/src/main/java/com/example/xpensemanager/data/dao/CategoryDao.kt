package com.example.xpensemanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.xpensemanager.data.entities.Category
import com.example.xpensemanager.data.entities.Expense

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM expenses WHERE category = :categoryName")
    suspend fun getExpensesForCategory(categoryName: String): List<Expense>

    @Delete
    suspend fun delete(category: Category)
}




