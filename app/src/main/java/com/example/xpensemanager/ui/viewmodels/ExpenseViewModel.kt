package com.example.xpensemanager.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.xpensemanager.data.db.XpenseDatabase
import com.example.xpensemanager.data.entities.Expense
import com.example.xpensemanager.data.dao.ExpenseCategoryTotal
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = XpenseDatabase.getDatabase(application).expenseDao()

    val allExpenses: LiveData<List<Expense>> = dao.getAllExpenses()
    val totalExpenses: LiveData<Double> = dao.getTotalExpenses()
    val totalCashExpenses: LiveData<Double> = dao.getTotalCashExpenses()
    val totalUpiExpenses: LiveData<Double> = dao.getTotalUpiExpenses()
    val expenseByCategory: LiveData<List<ExpenseCategoryTotal>> = dao.getExpenseByCategory()

    fun insertExpense(expense: Expense) {
        viewModelScope.launch { dao.insert(expense) }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { dao.delete(expense) }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch { dao.update(expense) }

    }
}
