package com.example.wordsnap

import android.os.Bundle
import android.view.View
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
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .addToBackStack("HOME")
            .commit()
    }
}
