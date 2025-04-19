package com.example.wordsnap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.auth.PasswordHelper
import com.example.domain.repository.WordSnapRepositoryImplementation
import com.example.domain.validation.ValidationService

class RegisterActivity : AppCompatActivity() {

    private val validationService = ValidationService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var repo = WordSnapRepositoryImplementation(this)

        val editTextName = findViewById<EditText>(R.id.editTextNameRegister)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmailRegister)
        val editTextPassword = findViewById<EditText>(R.id.editTextPasswordRegister)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonBackRegister = findViewById<Button>(R.id.buttonBackRegister)

        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            val nameResult = validationService.validateUsername(name)
            val emailResult = validationService.validateEmail(email)
            val passwordResult = validationService.validatePassword(password)

            if (!nameResult.isValid) {
                Toast.makeText(this, nameResult.errorMessage, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!emailResult.isValid) {
                Toast.makeText(this, emailResult.errorMessage, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!passwordResult.isValid) {
                Toast.makeText(this, passwordResult.errorMessage, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val salt = PasswordHelper.generateSalt()
            val passwordHash = PasswordHelper.hashPassword(password, salt)
            val success = repo.registerUser(name, email, passwordHash, salt)

            if (success) {
                Toast.makeText(this, "Реєстрація успішна", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Користувач з такою поштою вже існує", Toast.LENGTH_SHORT).show()
            }
        }

        buttonBackRegister.setOnClickListener {
            finish()
        }
    }
}
