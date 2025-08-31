package com.example.xpensemanager.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.xpensemanager.data.db.XpenseDatabase
import com.example.xpensemanager.data.entities.Category
import com.example.xpensemanager.data.entities.Expense
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = XpenseDatabase.getDatabase(application).categoryDao()
    val allCategories: LiveData<List<Category>> = dao.getAllCategories()

    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> get() = _expenses

    fun insertCategory(category: Category) {
        viewModelScope.launch { dao.insert(category) }
    }

    fun getExpensesForCategory(categoryName: String) {
        viewModelScope.launch {
            _expenses.postValue(dao.getExpensesForCategory(categoryName))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch { dao.delete(category) }
    }
}
