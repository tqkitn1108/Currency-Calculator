package com.example.currencycalculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var sourceAmountEditText: EditText
    private lateinit var targetAmountEditText: EditText
    private lateinit var sourceCurrencySpinner: Spinner
    private lateinit var targetCurrencySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Currency"
        }

        sourceAmountEditText = findViewById(R.id.sourceAmountEditText)
        targetAmountEditText = findViewById(R.id.targetAmountEditText)
        sourceCurrencySpinner = findViewById(R.id.sourceCurrencySpinner)
        targetCurrencySpinner = findViewById(R.id.targetCurrencySpinner)

        // Cài đặt danh sách các loại tiền tệ cho Spinner
        val currencyList = arrayOf("USD", "EUR", "VND", "JPY")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sourceCurrencySpinner.adapter = adapter
        targetCurrencySpinner.adapter = adapter

        // Sự kiện khi nhập số tiền
        sourceAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (sourceAmountEditText.isFocused) {
                    updateConversion(isSourceToTarget = true)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        targetAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (targetAmountEditText.isFocused) {
                    updateConversion(isSourceToTarget = false)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Sự kiện khi chọn đồng tiền
        sourceCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                updateConversion(isSourceToTarget = false)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        targetCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                updateConversion(isSourceToTarget = true)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateConversion(isSourceToTarget: Boolean) {
        val sourceAmountText = if (isSourceToTarget) sourceAmountEditText.text.toString() else targetAmountEditText.text.toString()
        if (sourceAmountText.isNotEmpty()) {
            val sourceAmount = sourceAmountText.toDoubleOrNull() ?: 0.0
            val sourceCurrency = if (isSourceToTarget) sourceCurrencySpinner.selectedItem.toString() else targetCurrencySpinner.selectedItem.toString()
            val targetCurrency = if (isSourceToTarget) targetCurrencySpinner.selectedItem.toString() else sourceCurrencySpinner.selectedItem.toString()

            val conversionRate = getConversionRate(sourceCurrency, targetCurrency)
            val targetAmount = sourceAmount * conversionRate

            if (isSourceToTarget) {
                targetAmountEditText.setText(String.format(Locale.US, "%.2f", targetAmount))
            } else {
                sourceAmountEditText.setText(String.format(Locale.US, "%.2f", targetAmount))
            }
        } else {
            if (isSourceToTarget) {
                targetAmountEditText.setText("")
            } else {
                sourceAmountEditText.setText("")
            }
        }
    }

    // Hàm giả lập tỷ giá chuyển đổi của các loại đơn vị tiền tệ
    private fun getConversionRate(fromCurrency: String, toCurrency: String): Double {
        return when (fromCurrency to toCurrency) {
            "USD" to "EUR" -> 0.9239
            "EUR" to "USD" -> 1.0824
            "EUR" to "VND" -> 27432.6226
            "VND" to "EUR" -> 0.00003645
            "USD" to "VND" -> 25345.0
            "VND" to "USD" -> 0.00003946
            "JPY" to "VND" -> 165.7077
            "VND" to "JPY" -> 0.006035
            else -> 1.0
        }
    }
}