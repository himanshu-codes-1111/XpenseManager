package com.example.xpensemanager.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.*
import com.example.xpensemanager.R
import com.example.xpensemanager.data.entities.Expense
import java.util.*

class AddExpenseDialog(
    context: Context,
    private val categories: List<String>,
    private val onExpenseAdded: (Expense) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_expense)

        val amountInput = findViewById<EditText>(R.id.inputAmount)
        val descriptionInput = findViewById<EditText>(R.id.inputDescription)
        val categorySpinner = findViewById<Spinner>(R.id.spinnerCategory)
        val paymentMethodRadio = findViewById<RadioGroup>(R.id.radioGroupPayment)
        val btnAdd = findViewById<Button>(R.id.btnAddExpense)

        categorySpinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, categories)

        btnAdd.setOnClickListener {
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()
            val category = categorySpinner.selectedItem.toString()
            val paymentMethod = if (paymentMethodRadio.checkedRadioButtonId == R.id.radioCash) "Cash" else "UPI"

            if (amount != null && category.isNotEmpty()) {
                val expense = Expense(
                    amount = amount,
                    category = category,
                    description = description,
                    timestamp = System.currentTimeMillis(),
                    paymentMethod = paymentMethod
                )
                onExpenseAdded(expense)
                dismiss()
            } else {
                Toast.makeText(context, "Enter valid details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
