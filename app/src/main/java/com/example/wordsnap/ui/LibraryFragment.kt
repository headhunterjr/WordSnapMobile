package com.example.wordsnap.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import java.time.LocalDateTime

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
                val et = EditText(requireContext()).apply {
                    hint = "New set name"
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                AlertDialog.Builder(requireContext())
                    .setTitle("Create Cardset")
                    .setView(et)
                    .setPositiveButton("Create") { _, _ ->
                        val name = et.text.toString().trim().takeIf(String::isNotEmpty)
                            ?: return@setPositiveButton
                        val newId = repo.addCardset(
                            Cardset(
                                id = 0,
                                userRef = UserSession.userId!!.toInt(),
                                name = name,
                                isPublic = true,
                                createdAt = LocalDateTime.now().toString()
                            )
                        )
                        requireActivity().supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, CardsetDetailFragment.newInstance(newId))
                            .addToBackStack(null)
                            .commit()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
    }
}
