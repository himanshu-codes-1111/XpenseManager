package com.example.xpensemanager.ui.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.xpensemanager.R
import com.example.xpensemanager.data.entities.Expense
import com.example.xpensemanager.ui.viewmodels.ExpenseViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import java.text.SimpleDateFormat
import java.util.*

class EditExpenseActivity : AppCompatActivity() {

    private lateinit var amountInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var paymentMethodToggle: MaterialButtonToggleGroup
    private lateinit var btnCash: MaterialButton
    private lateinit var btnUpi: MaterialButton
    private lateinit var btnUpdate: Button
    private lateinit var dateText: TextView
    private lateinit var timeText: TextView
    private lateinit var backButton: ImageView

    private val calendar = Calendar.getInstance()
    private lateinit var categories: List<String>
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private var expenseId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)

        amountInput = findViewById(R.id.inputAmount)
        descriptionInput = findViewById(R.id.inputDescription)
        categorySpinner = findViewById(R.id.spinnerCategory)
        paymentMethodToggle = findViewById(R.id.paymentMethodGroup)
        btnCash = findViewById(R.id.btnCash)
        btnUpi = findViewById(R.id.btnUpi)
        btnUpdate = findViewById(R.id.btnUpdateExpense)
        dateText = findViewById(R.id.textDate)
        timeText = findViewById(R.id.textTime)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        categories = intent.getStringArrayListExtra("categoryList") ?: listOf("Others")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = adapter

        val expense = intent.getSerializableExtra("expense") as? Expense
        expense?.let {
            expenseId = it.id
            amountInput.setText(it.amount.toString())
            descriptionInput.setText(it.description)
            categorySpinner.setSelection(categories.indexOf(it.category))
            dateText.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it.timestamp))
            timeText.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it.timestamp))
            calendar.timeInMillis = it.timestamp

            stylePaymentButtons()

            when (it.paymentMethod) {
                "Cash" -> {
                    btnCash.isChecked = true
                    applySelectedButtonStyle(btnCash)
                    resetButtonStyle(btnUpi)
                }
                "UPI" -> {
                    btnUpi.isChecked = true
                    applySelectedButtonStyle(btnUpi)
                    resetButtonStyle(btnCash)
                }
            }
        }

        dateText.setOnClickListener { showDatePicker() }
        timeText.setOnClickListener { showTimePicker() }

        paymentMethodToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val selectedButton = findViewById<MaterialButton>(checkedId)
                applySelectedButtonStyle(selectedButton)

                if (checkedId == R.id.btnCash) {
                    resetButtonStyle(btnUpi)
                } else if (checkedId == R.id.btnUpi) {
                    resetButtonStyle(btnCash)
                }
            }
        }

        btnUpdate.setOnClickListener {
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()
            val category = categorySpinner.selectedItem?.toString() ?: "Others"
            val paymentMethod = when (paymentMethodToggle.checkedButtonId) {
                R.id.btnCash -> "Cash"
                R.id.btnUpi -> "UPI"
                else -> "Others"
            }

            if (amount != null && expenseId != -1) {
                val updatedExpense = Expense(
                    id = expenseId,
                    amount = amount,
                    category = category,
                    description = description,
                    timestamp = calendar.timeInMillis,
                    paymentMethod = paymentMethod
                )
                expenseViewModel.updateExpense(updatedExpense)
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Enter valid details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                dateText.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            this,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                timeText.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun stylePaymentButtons() {
        btnCash.setStrokeColorResource(android.R.color.darker_gray)
        btnCash.setTextColor(resources.getColor(android.R.color.darker_gray))
        btnCash.setTypeface(null, Typeface.NORMAL)
        btnCash.setBackgroundColor(Color.LTGRAY)

        btnUpi.setStrokeColorResource(android.R.color.darker_gray)
        btnUpi.setTextColor(resources.getColor(android.R.color.darker_gray))
        btnUpi.setTypeface(null, Typeface.NORMAL)
        btnUpi.setBackgroundColor(Color.LTGRAY)
    }

    private fun applySelectedButtonStyle(button: MaterialButton) {
        button.setTextColor(resources.getColor(android.R.color.black))
        button.setTypeface(null, Typeface.BOLD)
        button.setStrokeColorResource(android.R.color.black)
        button.setBackgroundColor(Color.parseColor("#DDDDDD"))
        button.setRippleColor(null)
    }

    private fun resetButtonStyle(button: MaterialButton) {
        button.setTextColor(resources.getColor(android.R.color.darker_gray))
        button.setTypeface(null, Typeface.NORMAL)
        button.setStrokeColorResource(android.R.color.darker_gray)
        button.setBackgroundColor(Color.TRANSPARENT)
        button.setRippleColor(null)
    }
}
