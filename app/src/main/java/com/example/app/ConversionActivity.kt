package com.example.app
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import java.text.DecimalFormat

class ConversionActivity : AppCompatActivity() {

    private lateinit var aedEditText: EditText
    private lateinit var cadEditText: EditText
    private lateinit var usdEditText: EditText
    private lateinit var eurEditText: EditText
    private lateinit var sarEditText: EditText
    private lateinit var kdEditText: EditText

    private val conversionRates = mapOf(
        Currency.AED to 3.67,
        Currency.CAD to 1.25,
        Currency.USD to 1.0,
        Currency.EUR to 0.85,
        Currency.SAR to 3.75,
        Currency.KD to 0.30
    )

    private lateinit var aedTextWatcher: TextWatcher
    private lateinit var cadTextWatcher: TextWatcher
    private lateinit var usdTextWatcher: TextWatcher
    private lateinit var eurTextWatcher: TextWatcher
    private lateinit var sarTextWatcher: TextWatcher
    private lateinit var kdTextWatcher: TextWatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversion)

        aedEditText = findViewById(R.id.aedEditText)
        cadEditText = findViewById(R.id.cadEditText)
        usdEditText = findViewById(R.id.usdEditText)
        eurEditText = findViewById(R.id.eurEditText)
        sarEditText = findViewById(R.id.sarEditText)
        kdEditText = findViewById(R.id.kdEditText)

        setCurrencyTextWatchers()

    }

    private fun setCurrencyTextWatchers() {
        aedTextWatcher = createTextWatcher(aedEditText, Currency.AED)
        cadTextWatcher = createTextWatcher(cadEditText, Currency.CAD)
        usdTextWatcher = createTextWatcher(usdEditText, Currency.USD)
        eurTextWatcher = createTextWatcher(eurEditText, Currency.EUR)
        sarTextWatcher = createTextWatcher(sarEditText, Currency.SAR)
        kdTextWatcher = createTextWatcher(kdEditText, Currency.KD)

        aedEditText.addTextChangedListener(aedTextWatcher)
        cadEditText.addTextChangedListener(cadTextWatcher)
        usdEditText.addTextChangedListener(usdTextWatcher)
        eurEditText.addTextChangedListener(eurTextWatcher)
        sarEditText.addTextChangedListener(sarTextWatcher)
        kdEditText.addTextChangedListener(kdTextWatcher)
    }

    private fun createTextWatcher(editText: EditText, currency: Currency): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (editText.isFocused) {
                    val value = s.toString().toDoubleOrNull()
                    if (value != null) {
                        updateCurrencies(value, currency)
                    } else {
                        clearAllTextFields()
                    }
                }
            }
        }
    }

    private fun updateCurrencies(value: Double, currency: Currency) {
        val baseValue = value / conversionRates[currency]!!
        val formatter = DecimalFormat("#.###")

        removeTextWatchers()

        aedEditText.setTextIfNotFocused(formatter.format(baseValue * conversionRates[Currency.AED]!!))
        cadEditText.setTextIfNotFocused(formatter.format(baseValue * conversionRates[Currency.CAD]!!))
        usdEditText.setTextIfNotFocused(formatter.format(baseValue * conversionRates[Currency.USD]!!))
        eurEditText.setTextIfNotFocused(formatter.format(baseValue * conversionRates[Currency.EUR]!!))
        sarEditText.setTextIfNotFocused(formatter.format(baseValue * conversionRates[Currency.SAR]!!))
        kdEditText.setTextIfNotFocused(formatter.format(baseValue * conversionRates[Currency.KD]!!))

        addTextWatchers()
    }

    private fun clearAllTextFields() {
        removeTextWatchers()

        aedEditText.setText("")
        cadEditText.setText("")
        usdEditText.setText("")
        eurEditText.setText("")
        sarEditText.setText("")
        kdEditText.setText("")

        addTextWatchers()
    }

    private fun removeTextWatchers() {
        aedEditText.removeTextChangedListener(aedTextWatcher)
        cadEditText.removeTextChangedListener(cadTextWatcher)
        usdEditText.removeTextChangedListener(usdTextWatcher)
        eurEditText.removeTextChangedListener(eurTextWatcher)
        sarEditText.removeTextChangedListener(sarTextWatcher)
        kdEditText.removeTextChangedListener(kdTextWatcher)
    }

    private fun addTextWatchers() {
        aedEditText.addTextChangedListener(aedTextWatcher)
        cadEditText.addTextChangedListener(cadTextWatcher)
        usdEditText.addTextChangedListener(usdTextWatcher)
        eurEditText.addTextChangedListener(eurTextWatcher)
        sarEditText.addTextChangedListener(sarTextWatcher)
        kdEditText.addTextChangedListener(kdTextWatcher)
    }

    private fun EditText.setTextIfNotFocused(text: String) {
        if (!this.isFocused) {
            this.setText(text)
        }
    }

    enum class Currency {
        AED, CAD, USD, EUR, SAR, KD
    }



    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_home -> {
                    // Handle Home action
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }


    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
