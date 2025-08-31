package com.example.xpensemanager.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensemanager.R
import com.example.xpensemanager.data.entities.Expense
import com.example.xpensemanager.ui.activities.AddExpenseActivity
import com.example.xpensemanager.ui.activities.EditExpenseActivity
import com.example.xpensemanager.ui.adapters.ExpenseAdapter
import com.example.xpensemanager.ui.viewmodels.ExpenseViewModel
import com.example.xpensemanager.ui.viewmodels.CategoryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExpenseFragment : Fragment() {

    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter
    private lateinit var fabAddExpense: FloatingActionButton
    private lateinit var searchEditText: EditText
    private var categoryList: List<String> = emptyList()
    private var allExpenses: List<Expense> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        fabAddExpense = view.findViewById(R.id.fabAddExpense)
        searchEditText = view.findViewById(R.id.searchEditText)

        adapter = ExpenseAdapter(emptyList(), ::showDeleteConfirmationDialog, categoryList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)

        expenseViewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            allExpenses = expenses
            adapter.updateExpenses(expenses)
        }

        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            categoryList = categories.map { it.name }
            adapter = ExpenseAdapter(allExpenses, ::showDeleteConfirmationDialog, categoryList)
            recyclerView.adapter = adapter
        }

        fabAddExpense.setOnClickListener {
            val intent = Intent(requireContext(), AddExpenseActivity::class.java)
            intent.putStringArrayListExtra("categoryList", ArrayList(categoryList))
            startActivity(intent)
        }

        setupSearchFunctionality()
    }

    private fun openEditExpense(expense: Expense) {
        val intent = Intent(requireContext(), EditExpenseActivity::class.java).apply {
            putExtra("expense", expense)
            putStringArrayListExtra("categoryList", ArrayList(categoryList))
        }
        startActivity(intent)
    }

    private fun setupSearchFunctionality() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { filterExpenses(it.toString()) }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterExpenses(query: String) {
        val filteredList = allExpenses.filter { expense ->
            expense.description.contains(query, ignoreCase = true) ||
                    expense.category.contains(query, ignoreCase = true) ||
                    expense.amount.toString().contains(query, ignoreCase = true) ||
                    expense.paymentMethod.contains(query, ignoreCase = true)
        }

        adapter.updateExpenses(filteredList)

        recyclerView.post {
            adapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(0)
        }
    }

    private fun showDeleteConfirmationDialog(expense: Expense) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<View>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnRemove).setOnClickListener {
            expenseViewModel.deleteExpense(expense)
            dialog.dismiss()
        }

        dialog.show()
    }
}
