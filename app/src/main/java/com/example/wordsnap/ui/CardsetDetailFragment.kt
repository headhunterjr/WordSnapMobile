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
import com.example.data.entities.Card
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
    private lateinit var saveButton: ImageButton

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
        saveButton = view.findViewById(R.id.buttonSaveCardset)

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

            // Setup save button only for logged in users
            setupSaveButton(cardset)
        } else {
            btnTest.visibility = View.GONE
            saveButton.visibility = View.GONE
        }
    }

    private fun setupSaveButton(cardset: Cardset) {
        // Only show save button if the user is not the owner
        if (UserSession.userId == cardset.userRef.toLong()) {
            saveButton.visibility = View.GONE
            return
        }

        saveButton.visibility = View.VISIBLE
        updateSaveButtonState()

        saveButton.setOnClickListener {
            val userId = UserSession.userId!!
            val isInLibrary = repo.isCardsetInLibrary(userId, cardset.id)

            if (isInLibrary) {
                // Remove from library
                repo.removeCardsetFromLibrary(userId, cardset.id)
            } else {
                // Add to library
                repo.saveCardsetToLibrary(userId, cardset.id)
            }

            // Update button state
            updateSaveButtonState()
        }
    }

    private fun updateSaveButtonState() {
        val isInLibrary = UserSession.userId?.let { repo.isCardsetInLibrary(it, cardsetId) }

        if (isInLibrary == true) {
            // Cardset is saved - show "bookmark filled" icon
            saveButton.setImageResource(R.drawable.ic_bookmark_filled)
            saveButton.contentDescription = getString(R.string.remove_from_library)
        } else {
            // Cardset is not saved - show "bookmark outline" icon
            saveButton.setImageResource(R.drawable.ic_bookmark_outline)
            saveButton.contentDescription = getString(R.string.add_to_library)
        }
    }

    private fun setupCardset(cardset: Cardset) {
        cardsetTitle.text = cardset.name

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
                    .setTitle("Змінити назву набору")
                    .setView(et)
                    .setPositiveButton("Зберегти") { _, _ ->
                        val newName = et.text.toString().trim()
                        repo.updateCardsetName(cardset.id, newName)
                        cardsetTitle.text = newName
                    }
                    .setNegativeButton("Назад", null)
                    .show()
            }

            btnDeleteSet.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Видалити цей набір?")
                    .setMessage("Ця дія є незворотною.")
                    .setPositiveButton("Видалити") { _, _ ->
                        repo.deleteCardset(cardset.id)
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    .setNegativeButton("Назад", null)
                    .show()
            }
        }

        val cards = repo.getCardsForCardset(cardset.id)
        viewPager.adapter = CardAdapter(
            cards = cards,
            isOwner = isOwner,
            onEditCard = { card ->
                val dialogView = layoutInflater.inflate(R.layout.dialog_edit_card, null)
                val editTextEn = dialogView.findViewById<EditText>(R.id.editTextWordEn)
                val editTextUa = dialogView.findViewById<EditText>(R.id.editTextWordUa)
                val editTextNote = dialogView.findViewById<EditText>(R.id.editTextNote)

                editTextEn.setText(card.wordEn)
                editTextUa.setText(card.wordUa)
                editTextNote.setText(card.note)

                AlertDialog.Builder(requireContext())
                    .setTitle("Змінити картку")
                    .setView(dialogView)
                    .setPositiveButton("Зберегти") { _, _ ->
                        repo.updateCard(card.copy(
                            wordEn = editTextEn.text.toString().trim(),
                            wordUa = editTextUa.text.toString().trim(),
                            note = editTextNote.text.toString().trim()
                        ))
                        setupCardset(repo.getCardsetById(cardset.id)!!)
                    }
                    .setNegativeButton("Назад", null)
                    .show()
            },
            onDeleteCard = { card ->
                repo.deleteCard(card.id)
                setupCardset(repo.getCardsetById(cardset.id)!!)
            },
            onAddCard = {
                val dialogView = layoutInflater.inflate(R.layout.dialog_edit_card, null)
                val editTextEn = dialogView.findViewById<EditText>(R.id.editTextWordEn)
                val editTextUa = dialogView.findViewById<EditText>(R.id.editTextWordUa)
                val editTextNote = dialogView.findViewById<EditText>(R.id.editTextNote)

                AlertDialog.Builder(requireContext())
                    .setTitle("Додати нову картку")
                    .setView(dialogView)
                    .setPositiveButton("Додати") { _, _ ->
                        val wordEn = editTextEn.text.toString().trim().takeIf(String::isNotEmpty)
                            ?: return@setPositiveButton
                        val wordUa = editTextUa.text.toString().trim().takeIf(String::isNotEmpty)
                            ?: return@setPositiveButton
                        val note = editTextNote.text.toString().trim()

                        repo.addCard(
                            Card(
                                id = 0,
                                cardsetRef = cardset.id,
                                wordEn = wordEn,
                                wordUa = wordUa,
                                note = note
                            )
                        )
                        setupCardset(repo.getCardsetById(cardset.id)!!)
                    }
                    .setNegativeButton("Назад", null)
                    .show()
            }
        )
    }
}