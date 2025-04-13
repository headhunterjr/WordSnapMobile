package com.example.wordsnap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsnap.adapter.CardsetAdapter
import com.example.wordsnap.entities.Cardset
import com.example.wordsnap.database.DatabaseManager

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

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewCardsets)
        editTextSearch = findViewById(R.id.editTextSearch)
        buttonSearch = findViewById(R.id.buttonSearch)
        buttonLoginTop = findViewById(R.id.buttonLoginTop)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val randomCardsets = dbManager.getRandomPublicCardsets(4)
        displayCardsets(randomCardsets)

        // Search button logic
        buttonSearch.setOnClickListener {
            val query = editTextSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                val results = dbManager.searchPublicCardsets(query)
                displayCardsets(results)
            } else {
                // If the search bar is empty, maybe reload random
                val randomAgain = dbManager.getRandomPublicCardsets(4)
                displayCardsets(randomAgain)
            }
        }

        // Login button logic
        buttonLoginTop.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun displayCardsets(cardsets: List<Cardset>) {
        val adapter = CardsetAdapter(cardsets) { cardset ->
            // On item click. For now, just do a toast or do nothing
            // e.g. Toast.makeText(this, "Clicked: ${cardset.name}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
    }
}
