package com.example.wordsnap.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsnap.R
import com.example.wordsnap.adapter.CardsetAdapter
import com.example.domain.repository.WordSnapRepositoryImplementation
import com.example.wordsnap.LoginActivity

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var repo: WordSnapRepositoryImplementation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repo = WordSnapRepositoryImplementation(requireContext())

        val btnLogin = view.findViewById<Button>(R.id.buttonLoginTop)
        val etSearch = view.findViewById<EditText>(R.id.editTextSearch)
        val btnSearch = view.findViewById<Button>(R.id.buttonSearch)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewHome)

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = CardsetAdapter(repo.getRandomPublicCardsets(4)) { /* stub */ }

        btnLogin.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        btnSearch.setOnClickListener {
            val q = etSearch.text.toString().trim()
            val data = if (q.isEmpty())
                repo.getRandomPublicCardsets(4)
            else
                repo.searchPublicCardsets(q)
            rv.adapter = CardsetAdapter(data) { /* stub */ }
        }
    }
}
