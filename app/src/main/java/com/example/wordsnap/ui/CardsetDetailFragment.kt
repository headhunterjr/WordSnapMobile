package com.example.wordsnap.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.wordsnap.R
import com.example.wordsnap.adapter.CardAdapter
import com.example.data.entities.Cardset
import com.example.domain.auth.UserSession
import com.example.domain.repository.WordSnapRepositoryImplementation

class CardsetDetailFragment : Fragment(R.layout.fragment_cardset_detail) {

    private lateinit var repo: WordSnapRepositoryImplementation
    private lateinit var viewPager: ViewPager2
    private lateinit var cardsetTitle: TextView
    private var cardsetId: Int = -1

    companion object {
        private const val ARG_CARDSET_ID = "cardset_id"

        fun newInstance(cardsetId: Int): CardsetDetailFragment {
            return CardsetDetailFragment().apply {
                arguments = bundleOf(ARG_CARDSET_ID to cardsetId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardsetId = arguments?.getInt(ARG_CARDSET_ID) ?: -1
        if (cardsetId == -1) {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repo = WordSnapRepositoryImplementation(requireContext())

        cardsetTitle = view.findViewById(R.id.textViewCardsetTitle)
        viewPager = view.findViewById(R.id.viewPagerCards)

        val cardset = repo.getCardsetById(cardsetId)!!
        setupCardset(cardset)

        val btnTest = view.findViewById<Button>(R.id.buttonTakeTest)
        if (UserSession.isLoggedIn) {
            btnTest.visibility = View.VISIBLE
            btnTest.setOnClickListener {
                val frag = TestFragment.newInstance(cardset.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, frag)
                    .addToBackStack(null)
                    .commit()
            }
        } else {
            btnTest.visibility = View.GONE
        }
    }

    private fun setupCardset(cardset: Cardset) {
        // Title
        cardsetTitle.text = cardset.name

        // Owner check
        val isOwner = UserSession.userId == cardset.userRef.toLong()
        val btnEditSet   = requireView().findViewById<ImageButton>(R.id.buttonEditSet)
        val btnDeleteSet = requireView().findViewById<ImageButton>(R.id.buttonDeleteSet)

        btnEditSet.visibility   = if (isOwner) View.VISIBLE else View.GONE
        btnDeleteSet.visibility = if (isOwner) View.VISIBLE else View.GONE

        if (isOwner) {
            btnEditSet.setOnClickListener {
                val et = EditText(requireContext()).apply {
                    setText(cardset.name)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                AlertDialog.Builder(requireContext())
                    .setTitle("Rename this set")
                    .setView(et)
                    .setPositiveButton("Save") { _, _ ->
                        val newName = et.text.toString().trim()
                        repo.updateCardsetName(cardset.id, newName)
                        cardsetTitle.text = newName
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            btnDeleteSet.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete this set?")
                    .setMessage("This will remove the entire cardset.")
                    .setPositiveButton("Delete") { _, _ ->
                        repo.deleteCardset(cardset.id)
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }

        // build and set your CardAdapter exactly once
        val cards = repo.getCardsForCardset(cardset.id)
        viewPager.adapter = CardAdapter(
            cards       = cards,
            isOwner     = isOwner,
            onEditCard  = { card ->
                val dialogView = layoutInflater.inflate(R.layout.dialog_edit_card, null)
                val etEn = dialogView.findViewById<EditText>(R.id.editTextWordEn)
                val etUa = dialogView.findViewById<EditText>(R.id.editTextWordUa)
                etEn.setText(card.wordEn)
                etUa.setText(card.wordUa)

                AlertDialog.Builder(requireContext())
                    .setTitle("Edit this card")
                    .setView(dialogView)
                    .setPositiveButton("Save") { _, _ ->
                        repo.updateCard(card.copy(
                            wordEn = etEn.text.toString().trim(),
                            wordUa = etUa.text.toString().trim()
                        ))
                        // refresh
                        setupCardset(repo.getCardsetById(cardset.id)!!)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onDeleteCard = { card ->
                repo.deleteCard(card.id)
                // just refresh the pager
                setupCardset(repo.getCardsetById(cardset.id)!!)
            },
            onAddCard   = {
                // stub for now
            }
        )

        // no need to re-register the page-change callback every time
    }

}