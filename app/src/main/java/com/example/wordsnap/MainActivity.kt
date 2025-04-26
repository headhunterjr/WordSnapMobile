package com.example.wordsnap

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.auth.UserSession
import com.example.wordsnap.ui.HomeFragment
import com.example.wordsnap.ui.LibraryFragment
import com.example.wordsnap.ui.SavedFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserSession.init(this)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    private fun setupToolbar() {
        val btnLogin = findViewById<Button>(R.id.buttonLoginTop)

        updateLoginButton(btnLogin)

        btnLogin.setOnClickListener {
            if (UserSession.isLoggedIn) {
                UserSession.logout(this)
                updateLoginButton(btnLogin)
                recreate()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun updateLoginButton(button: Button) {
        button.text = if (UserSession.isLoggedIn) "Вийти" else "Увійти"
    }

    private fun setupBottomNavigation() {
        val nav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        if (!UserSession.isLoggedIn) {
            nav.visibility = View.GONE
        } else {
            nav.visibility = View.VISIBLE
        }

        nav.setOnNavigationItemSelectedListener {
            val frag = when (it.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_saved -> SavedFragment()
                R.id.nav_mine -> LibraryFragment()
                else -> HomeFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, frag)
                .commit()
            true
        }
        nav.selectedItemId = R.id.nav_home
    }
}