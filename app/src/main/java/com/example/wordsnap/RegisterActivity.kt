package com.example.wordsnap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.auth.PasswordHelper
import com.example.data.database.DatabaseManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbManager = DatabaseManager(this)

        val editTextName = findViewById<EditText>(R.id.editTextNameRegister)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmailRegister)
        val editTextPassword = findViewById<EditText>(R.id.editTextPasswordRegister)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonBackRegister = findViewById<Button>(R.id.buttonBackRegister)

        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val salt = PasswordHelper.generateSalt()
                val passwordHash = PasswordHelper.hashPassword(password, salt)
                val success = dbManager.registerUser(name, email, passwordHash, salt)
                if (success) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "User with this email already exists", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        buttonBackRegister.setOnClickListener {
            finish()
        }
    }
}
