package com.example.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class LoginActivityNoPref : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences("com.example.app", MODE_PRIVATE)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val configureEmailButton = findViewById<Button>(R.id.configureEmailButton)

        val welcomeEditText = findViewById<TextView>(R.id.welcomeEditText)

        val menuButton = findViewById<Button>(R.id.menuButton)

        welcomeEditText.text = ""

        configureEmailButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()
            if (email.isNotEmpty() && name.isNotEmpty()) {
                with(sharedPreferences.edit()) {
                    putString("email", email)
                    putString("Sales rep", name)
                    apply()
                }
                Log.d("LoginActivity", "Configure Email Button Clicked")
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("USER_EMAIL", email)
                    putExtra("USER_NAME", name)
                }
                startActivity(intent)
                finish()
            } else {
                if (email.isEmpty()) {
                    emailEditText.error = "Email is required"
                }
                if (name.isEmpty()) {
                    nameEditText.error = "Name is required"
                }
            }
        }

        menuButton.setOnClickListener {
            onBackPressed()
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("USER_EMAIL", sharedPreferences.getString("email", ""))
            putExtra("USER_NAME", sharedPreferences.getString("Sales rep", ""))
        }
        startActivity(intent)
        finish()
    }

}
