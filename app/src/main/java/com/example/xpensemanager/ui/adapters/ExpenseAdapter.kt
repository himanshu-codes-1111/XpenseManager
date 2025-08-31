package com.example.xpensemanager.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensemanager.R
import com.example.xpensemanager.data.entities.Expense
import com.example.xpensemanager.ui.activities.EditExpenseActivity
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(
    private var expenses: List<Expense>,
    private val onLongPressDelete: (Expense) -> Unit,
    private val categoryList: List<String>
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.imgPaymentMethod)
        val amount: TextView = view.findViewById(R.id.txtAmount)
        val category: TextView = view.findViewById(R.id.txtCategory)
        val description: TextView = view.findViewById(R.id.txtDescription)
        val date: TextView = view.findViewById(R.id.txtDate)
        val time: TextView = view.findViewById(R.id.txtTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        holder.icon.setImageResource(if (expense.paymentMethod == "Cash") R.drawable.ic_cash else R.drawable.ic_upi)
        holder.amount.text = "₹${expense.amount}"
        holder.category.text = expense.category
        holder.description.text = expense.description

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        holder.date.text = dateFormat.format(Date(expense.timestamp))
        holder.time.text = timeFormat.format(Date(expense.timestamp))

        holder.itemView.setOnLongClickListener {
            onLongPressDelete(expense)
            true
        }

        holder.itemView.setOnClickListener {
            val context = it.context
            val intent = Intent(context, EditExpenseActivity::class.java).apply {
                putExtra("expense", expense)
                putStringArrayListExtra("categoryList", ArrayList(categoryList))
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = expenses.size

    fun updateExpenses(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
