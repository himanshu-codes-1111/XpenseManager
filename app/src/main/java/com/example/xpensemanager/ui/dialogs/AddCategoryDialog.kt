package com.example.xpensemanager.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.xpensemanager.R
import com.example.xpensemanager.data.entities.Category

class AddCategoryDialog(
    context: Context,
    private val onCategoryAdded: (Category) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add_category)

        window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.8).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val categoryInput = findViewById<EditText>(R.id.edtCategoryName)
        val btnAdd = findViewById<Button>(R.id.btnAddCategory)
        val btnClose = findViewById<ImageView>(R.id.imgRemove)

        btnClose.setOnClickListener {
            dismiss()
        }

        btnAdd.setOnClickListener {
            val categoryName = categoryInput.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                onCategoryAdded(Category(name = categoryName))
                dismiss()
            } else {
                Toast.makeText(context, "Enter category name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
