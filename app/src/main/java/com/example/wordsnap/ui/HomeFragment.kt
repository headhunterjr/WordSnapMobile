package com.example.wordsnap.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsnap.R
import com.example.wordsnap.adapter.CardsetAdapter
import com.example.domain.repository.WordSnapRepositoryImplementation
import com.example.wordsnap.LoginActivity
import com.example.data.entities.Cardset
import com.example.domain.auth.UserSession

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var repo: WordSnapRepositoryImplementation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repo = WordSnapRepositoryImplementation(requireContext())

        val btnLogin = view.findViewById<Button>(R.id.buttonLoginTop)
        val etSearch = view.findViewById<EditText>(R.id.editTextSearch)
        val btnSearch = view.findViewById<ImageButton>(R.id.buttonSearch)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewHome)

        if (UserSession.isLoggedIn) {
            btnLogin.text = "Вийти"
            btnLogin.setOnClickListener {
                UserSession.logout(requireContext())
                requireActivity().recreate()
            }
        } else {
            btnLogin.text = "Увійти"
            btnLogin.setOnClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }

        val openDetail: (Cardset) -> Unit = { cardset ->
            val detail = repo.getCardsetById(cardset.id)
            val frag = CardsetDetailFragment.newInstance(cardset.id)
            if (detail == null) {
                Toast.makeText(requireContext(),
                    "Набір #${cardset.id} не знайдено!", Toast.LENGTH_SHORT).show()
            }
            else {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, frag)
                    .addToBackStack(null)
                    .commit()
            }
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = CardsetAdapter(repo.getRandomPublicCardsets(4), openDetail)

        btnSearch.setOnClickListener {
            val q = etSearch.text.toString().trim()
            val data = if (q.isEmpty())
                repo.getRandomPublicCardsets(4)
            else
                repo.searchPublicCardsets(q)

            rv.adapter = CardsetAdapter(data, openDetail)
        }
    }
}