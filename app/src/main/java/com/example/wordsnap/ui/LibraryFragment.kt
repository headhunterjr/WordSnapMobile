package com.example.wordsnap.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsnap.R
import com.example.wordsnap.adapter.CardsetAdapter
import com.example.data.entities.Cardset
import com.example.domain.auth.UserSession
import com.example.domain.repository.WordSnapRepositoryImplementation
import android.widget.TextView
import android.widget.Toast

class LibraryFragment : Fragment(R.layout.fragment_cardset_list) {
    private lateinit var repo: WordSnapRepositoryImplementation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repo = WordSnapRepositoryImplementation(requireContext())
        view.findViewById<TextView>(R.id.textTitle).text = getString(R.string.MyCardsets)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = GridLayoutManager(requireContext(), 1)

        val uid = UserSession.userId ?: -1L
        val list: List<Cardset> = repo.getMyCardsets(uid)

        rv.adapter = CardsetAdapter(
            cardsets    = list,
            onItemClick = { cardset ->
                val fragment = CardsetDetailFragment.newInstance(cardset.id)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onAddClick  = {
                Toast.makeText(requireContext(),
                    "Create new cardset → coming soon!",
                    Toast.LENGTH_SHORT).show()
            }
        )
    }
}
