package com.example.xpensemanager.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensemanager.R
import com.example.xpensemanager.data.entities.Category
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CategoryAdapter(
    private var categories: List<Category>,
    private val onCategorySelected: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedCategory: Category? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.chipGroup.removeAllViews()

        categories.forEach { category ->
            val chip = Chip(holder.chipGroup.context)
            chip.text = category.name
            chip.isCheckable = true
            chip.isChecked = category == selectedCategory

            chip.setOnClickListener {
                selectedCategory = category
                onCategorySelected(category)
                notifyDataSetChanged()
            }

            holder.chipGroup.addView(chip)
        }
    }

    override fun getItemCount() = 1

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chipGroup: ChipGroup = view.findViewById(R.id.chipGroup)
    }
}
