package com.example.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val menuButton = findViewById<Button>(R.id.menuButton)
        menuButton.visibility = View.GONE

        sharedPreferences = getSharedPreferences("com.example.app", MODE_PRIVATE)

        // Check if email and name are already stored
        val storedEmail = sharedPreferences.getString("email", null)
        val storedName = sharedPreferences.getString("Sales rep", null)

        if (!storedEmail.isNullOrEmpty() && !storedName.isNullOrEmpty()) {
            // Email and name are already configured, start MainActivity
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("USER_EMAIL", storedEmail)
                putExtra("USER_NAME", storedName)
            }
            startActivity(intent)
            finish()
            return
        }

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val configureEmailButton = findViewById<Button>(R.id.configureEmailButton)

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
    }
}
