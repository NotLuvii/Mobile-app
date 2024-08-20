package com.example.app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import java.text.DecimalFormat

class ConversionActivity : AppCompatActivity() {

    // Currency Conversion Elements
    private lateinit var aedEditText: EditText
    private lateinit var cadEditText: EditText
    private lateinit var usdEditText: EditText
    private lateinit var eurEditText: EditText
    private lateinit var sarEditText: EditText
    private lateinit var kdEditText: EditText

    // Density and Volume Calculation Elements
    private lateinit var volumeEditText: EditText
    private lateinit var resultEditText: EditText
    private lateinit var materialSpinner: Spinner

    // Unit Conversion Elements
    private lateinit var inputInches: EditText
    private lateinit var outputMM: TextView
    private lateinit var outputCM: TextView
    private lateinit var inputFeet: EditText
    private lateinit var outputCMFeet: TextView
    private lateinit var outputMeters: TextView

    private val conversionRates = mapOf(
        Currency.AED to 3.67,
        Currency.CAD to 1.25,
        Currency.USD to 1.0,
        Currency.EUR to 0.85,
        Currency.SAR to 3.75,
        Currency.KD to 0.30
    )

    private val densities = mapOf(
        "MSI" to 7.85,
        "SS-316L" to 8.0,
        "SS-420" to 7.75,
        "SS-17-4PH" to 7.8,
        "Ti6A14V-G23" to 4.43,
        "Cobalt Chrome MP 1" to 8.4,
        "IN-718" to 8.19,
        "IN-625" to 8.44,
        "AlSi10Mg" to 2.7
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

        // Initialize Currency Conversion Fields
        aedEditText = findViewById(R.id.aedEditText)
        cadEditText = findViewById(R.id.cadEditText)
        usdEditText = findViewById(R.id.usdEditText)
        eurEditText = findViewById(R.id.eurEditText)
        sarEditText = findViewById(R.id.sarEditText)
        kdEditText = findViewById(R.id.kdEditText)

        // Initialize Material Density Calculation Fields
        volumeEditText = findViewById(R.id.volumeEditText)
        resultEditText = findViewById(R.id.weightEditText)
        materialSpinner = findViewById(R.id.materialSpinner)

        // Initialize Unit Conversion Fields
        inputInches = findViewById(R.id.inputInches)
        outputMM = findViewById(R.id.outputMM)
        outputCM = findViewById(R.id.outputCM)
        inputFeet = findViewById(R.id.inputFeet)
        outputCMFeet = findViewById(R.id.outputCMFeet)
        outputMeters = findViewById(R.id.outputMeters)

        // Set up all the conversion mechanisms
        setCurrencyTextWatchers()
        setupMaterialSpinner()
        setupUnitConverters()


        findViewById<Button>(R.id.homeButton).setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupMaterialSpinner() {
        val materials = densities.keys.toTypedArray()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, materials)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        materialSpinner.adapter = adapter

        materialSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calculateAndSetResult()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        volumeEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                calculateAndSetResult()
            }
        })
    }

    private fun calculateAndSetResult() {
        val selectedMaterial = materialSpinner.selectedItem.toString()
        val volume = volumeEditText.text.toString().toDoubleOrNull()
        val density = densities[selectedMaterial]
        val formatter = DecimalFormat("#.###")

        if (volume != null && density != null) {
            val result = formatter.format(volume * density / 1000)
            resultEditText.setText(result.toString())
        } else {
            resultEditText.setText("")
        }
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

    private fun setupUnitConverters() {
        inputInches.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inches = s.toString().toDoubleOrNull()
                if (inches != null) {
                    outputMM.text = "MM:" + (inches * 25.4).formatAsString()
                    outputCM.text = "CM: " + (inches * 2.54).formatAsString()
                } else {
                    outputMM.text = "MM: "
                    outputCM.text = "CM: "
                }
            }
        })

        inputFeet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val feet = s.toString().toDoubleOrNull()
                if (feet != null) {
                    outputCMFeet.text = "CM: " + (feet * 30.48).formatAsString()
                    outputMeters.text = "M: " + (feet * 0.3048).formatAsString()
                } else {
                    outputCMFeet.text = "CM: "
                    outputMeters.text = "M: "
                }
            }
        })
    }

    private fun Double.formatAsString(): String {
        val formatter = DecimalFormat("#.###")
        return formatter.format(this)
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_home -> {
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
