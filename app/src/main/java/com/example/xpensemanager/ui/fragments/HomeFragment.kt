package com.example.xpensemanager.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.enums.LegendLayout
import com.example.xpensemanager.R
import com.example.xpensemanager.data.dao.ExpenseCategoryTotal
import com.example.xpensemanager.ui.viewmodels.ExpenseViewModel

class HomeFragment : Fragment() {

    private val expenseViewModel: ExpenseViewModel by viewModels()

    private lateinit var pieChart: AnyChartView
    private lateinit var txtTotalTransactions: TextView
    private lateinit var txtTotalSpending: TextView
    private lateinit var txtCashExpense: TextView
    private lateinit var txtUpiExpense: TextView
    private lateinit var loadingText: TextView

    private lateinit var categoryStatsLayout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pieChart = view.findViewById(R.id.pieChart)
        txtTotalTransactions = view.findViewById(R.id.txtTotalTransactions)
        txtTotalSpending = view.findViewById(R.id.txtTotalSpending)
        txtCashExpense = view.findViewById(R.id.txtCashExpense)
        txtUpiExpense = view.findViewById(R.id.txtUpiExpense)
        loadingText = view.findViewById(R.id.loadingText)
        categoryStatsLayout = view.findViewById(R.id.categoryStatsLayout)

        expenseViewModel.expenseByCategory.observe(viewLifecycleOwner, Observer { data ->
            Log.d("HomeFragment", "Pie Chart Data: $data")
            updatePieChart(data)
            updateCategoryStats(data)
        })

        expenseViewModel.allExpenses.observe(viewLifecycleOwner) {
            txtTotalTransactions.text = "Total Transactions: ${it.size}"
        }

        expenseViewModel.totalCashExpenses.observe(viewLifecycleOwner) { cash ->
            expenseViewModel.totalUpiExpenses.observe(viewLifecycleOwner) { upi ->
                txtCashExpense.text = "Cash: ₹$cash"
                txtUpiExpense.text = "UPI: ₹$upi"

                val totalSpending = cash + upi
                txtTotalSpending.text = "Total Spending: ₹$totalSpending"
            }
        }
    }

    private fun updatePieChart(categoryData: List<ExpenseCategoryTotal>) {

        loadingText.text = "Loading..."

        if (categoryData.isEmpty()) {
            Log.d("HomeFragment", "No data for Pie Chart")
            pieChart.visibility = View.GONE

            return
        } else {
            pieChart.visibility = View.VISIBLE
            loadingText.visibility = View.GONE
        }

        val chart = AnyChart.pie()
        chart.title("Expenses by Category")
        chart.legend()
            .itemsLayout(LegendLayout.HORIZONTAL)
            .position("center")

        Log.d("mytag",""+categoryData.size)

        val entries = categoryData.map {
            Log.d("mytag","${it.total}")
            ValueDataEntry(it.category, it.total)


        }
        Log.d("mytag",""+entries.size)
        chart.data(entries)

        pieChart.setChart(chart)
        pieChart.refreshDrawableState()
    }

    private fun updateCategoryStats(categoryData: List<ExpenseCategoryTotal>) {
        categoryStatsLayout.removeAllViews()
        categoryData.forEach {
            val textView = TextView(requireContext()).apply {
                text = "${it.category}: ₹${it.total}"
                textSize = 16f
                setPadding(0, 0, 0, 8)
            }
            categoryStatsLayout.addView(textView)
        }
    }

    override fun onResume() {
        super.onResume()
        expenseViewModel.expenseByCategory.value?.let {
            updatePieChart(it)
            updateCategoryStats(it)
        }
    }
}
