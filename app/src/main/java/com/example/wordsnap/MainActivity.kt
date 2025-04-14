package com.example.wordsnap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsnap.adapter.CardsetAdapter
import com.example.data.entities.Cardset
import com.example.data.database.DatabaseManager
import com.example.domain.auth.UserSession

class MainActivity : AppCompatActivity() {
    private lateinit var dbManager: DatabaseManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextSearch: EditText
    private lateinit var buttonSearch: Button
    private lateinit var buttonLoginTop: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbManager = DatabaseManager(this)

        recyclerView = findViewById(R.id.recyclerViewCardsets)
        editTextSearch = findViewById(R.id.editTextSearch)
        buttonSearch = findViewById(R.id.buttonSearch)
        buttonLoginTop = findViewById(R.id.buttonLoginTop)
        updateLoginButton()

        recyclerView.layoutManager = LinearLayoutManager(this)

        val randomCardsets = dbManager.getRandomPublicCardsets(4)
        displayCardsets(randomCardsets)

        buttonSearch.setOnClickListener {
            val query = editTextSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                val results = dbManager.searchPublicCardsets(query)
                displayCardsets(results)
            } else {
                val randomAgain = dbManager.getRandomPublicCardsets(4)
                displayCardsets(randomAgain)
            }
        }

        buttonLoginTop.setOnClickListener {
            if (UserSession.isLoggedIn) {
                UserSession.logout()
                updateLoginButton()
                recreate()
            }
            else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun displayCardsets(cardsets: List<Cardset>) {
        val adapter = CardsetAdapter(cardsets) { cardset ->
            // On item click. For now, just do a toast or do nothing
            // e.g. Toast.makeText(this, "Clicked: ${cardset.name}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
    }

    private fun updateLoginButton() {
        if (UserSession.isLoggedIn) {
            buttonLoginTop.text = "Вийти"
        } else {
            buttonLoginTop.text = "Увійти"
        }
    }
}
