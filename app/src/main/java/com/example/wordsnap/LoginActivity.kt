package com.example.wordsnap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.auth.PasswordHelper
import com.example.domain.auth.UserSession
import com.example.domain.repository.WordSnapRepositoryImplementation

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var repo = WordSnapRepositoryImplementation(this)

        val editTextEmail = findViewById<EditText>(R.id.editTextEmailLogin)
        val editTextPassword = findViewById<EditText>(R.id.editTextPasswordLogin)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonGoToRegister = findViewById<Button>(R.id.buttonGoToRegister)
        val buttonBackLogin = findViewById<ImageButton>(R.id.buttonBackLogin)

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val user = repo.getUserByEmail(email)
                if (user != null) {
                    if (PasswordHelper.verifyPassword(password, user.passwordHash, user.passwordSalt)) {
                        Toast.makeText(this, "Вхід успішно виконано", Toast.LENGTH_SHORT).show()
                        UserSession.login(user, this)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Неправильний пароль", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Неправильна пошта", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Введіть пошту та пароль", Toast.LENGTH_SHORT).show()
            }
        }

        buttonGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        buttonBackLogin.setOnClickListener {
            finish()
        }
    }
}
