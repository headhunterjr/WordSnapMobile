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

class SavedFragment : Fragment(R.layout.fragment_cardset_list) {
    private lateinit var repo: WordSnapRepositoryImplementation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repo = WordSnapRepositoryImplementation(requireContext())
        view.findViewById<TextView>(R.id.textTitle).text = getString(R.string.SavedCardsets)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = GridLayoutManager(requireContext(), 1)

        val uid = UserSession.userId ?: -1L
        Log.d("SavedFragment", "Current userId = $uid")

        val list = repo.getSavedCardsets(uid)
        Log.d("SavedFragment", "Fetched ${list.size} saved cardsets: ${list.map { it.name }}")

        rv.adapter = CardsetAdapter(list) { cardset ->
            val fragment = CardsetDetailFragment.newInstance(cardset.id)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
