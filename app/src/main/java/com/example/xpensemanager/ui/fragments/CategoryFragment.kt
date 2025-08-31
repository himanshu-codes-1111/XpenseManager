package com.example.xpensemanager.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensemanager.R
import com.example.xpensemanager.data.entities.Category
import com.example.xpensemanager.ui.adapters.ExpenseAdapter
import com.example.xpensemanager.ui.dialogs.AddCategoryDialog
import com.example.xpensemanager.ui.viewmodels.CategoryViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoryFragment : Fragment() {
    private val categoryViewModel: CategoryViewModel by viewModels()
    private lateinit var chipGroup: ChipGroup
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddCategory: FloatingActionButton
    private lateinit var expenseAdapter: ExpenseAdapter
    private var categoryList: List<String> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chipGroup = view.findViewById(R.id.chipGroup)
        recyclerView = view.findViewById(R.id.recyclerView)
        fabAddCategory = view.findViewById(R.id.fabAddCategory)

        expenseAdapter = ExpenseAdapter(emptyList(), { expense -> deleteExpense(expense) }, emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = expenseAdapter

        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            categoryList = categories.map { it.name }
            updateCategoryChips(categories)
        }

        fabAddCategory.setOnClickListener {
            AddCategoryDialog(requireContext()) { category ->
                categoryViewModel.insertCategory(category)
            }.show()
        }
    }

    private fun updateCategoryChips(categories: List<Category>) {
        chipGroup.removeAllViews()

        categories.forEach { category ->
            val chip = Chip(requireContext())
            chip.text = category.name
            chip.isCheckable = true

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    categoryViewModel.getExpensesForCategory(category.name)
                } else {
                    expenseAdapter.updateExpenses(emptyList())
                    recyclerView.visibility = View.GONE
                }
            }

            chip.setOnLongClickListener {
                showDeleteCategoryDialog(category)
                true
            }

            chipGroup.addView(chip)
        }

        categoryViewModel.expenses.observe(viewLifecycleOwner) { expenses ->
            recyclerView.visibility = if (expenses.isEmpty()) View.GONE else View.VISIBLE
            expenseAdapter.updateExpenses(expenses)
        }
    }

    private fun showDeleteCategoryDialog(category: Category) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_category, null)
        val dialog = android.app.Dialog(requireContext())

        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnRemove = dialogView.findViewById<Button>(R.id.btnRemove)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnRemove.setOnClickListener {
            categoryViewModel.deleteCategory(category)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteExpense(expense: com.example.xpensemanager.data.entities.Expense) {

    }
}
