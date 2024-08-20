package com.example.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val newInquiryButton = findViewById<Button>(R.id.newInquiryButton)
        newInquiryButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val conversionButton = findViewById<Button>(R.id.conversionButton)
        conversionButton.setOnClickListener {
            val intent = Intent(this, ConversionActivity::class.java)
            startActivity(intent)
            finish()
        }

        val testingDetailsButton = findViewById<Button>(R.id.testingDetailsButton)
        testingDetailsButton.setOnClickListener {
            val intent = Intent(this, TestingActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}