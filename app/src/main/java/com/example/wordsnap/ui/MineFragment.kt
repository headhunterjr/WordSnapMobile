// app/src/main/java/com/example/wordsnap/ui/MineFragment.kt
package com.example.wordsnap.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsnap.R
import com.example.wordsnap.adapter.CardsetAdapter
import com.example.domain.auth.UserSession
import com.example.domain.repository.WordSnapRepositoryImplementation
import android.widget.TextView

class MineFragment : Fragment(R.layout.fragment_cardset_list) {
    private lateinit var repo: WordSnapRepositoryImplementation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repo = WordSnapRepositoryImplementation(requireContext())
        view.findViewById<TextView>(R.id.textTitle).text = "My Cardsets"

        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = GridLayoutManager(requireContext(), 1)

        val uid = UserSession.userId ?: -1L
        Log.d("MineFragment", "Current userId = $uid")

        val list = repo.getMyCardsets(uid)
        Log.d("MineFragment", "Fetched ${list.size} own cardsets: ${list.map { it.name }}")

        rv.adapter = CardsetAdapter(list) { /* stub */ }
    }
}
