package com.example.wordsnap.ui

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.data.entities.Card
import com.example.wordsnap.R
import com.example.wordsnap.adapter.CardAdapter
import com.example.data.entities.Cardset
import com.example.domain.auth.UserSession
import com.example.domain.repository.WordSnapRepositoryImplementation
import com.example.domain.validation.ValidationService

class CardsetDetailFragment : Fragment(R.layout.fragment_cardset_detail) {

    private lateinit var repo: WordSnapRepositoryImplementation
    private lateinit var viewPager: ViewPager2
    private lateinit var cardsetTitle: TextView
    private var cardsetId: Int = -1
    private var currentCardPosition = 0
    private lateinit var saveButton: ImageButton
    private lateinit var privacyLabel: TextView
    private lateinit var privacySwitch: Switch
    private val validationService = ValidationService()

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
        privacyLabel = view.findViewById(R.id.textViewPrivacyLabel)
        privacySwitch = view.findViewById(R.id.switchPrivacy)

        registerForContextMenu(viewPager)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentCardPosition = position
            }
        })

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

            setupSaveButton(cardset)
        } else {
            btnTest.visibility = View.GONE
            saveButton.visibility = View.GONE
        }
    }

    private fun setupSaveButton(cardset: Cardset) {
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
                repo.removeCardsetFromLibrary(userId, cardset.id)
            } else {
                repo.saveCardsetToLibrary(userId, cardset.id)
            }
            updateSaveButtonState()
        }
    }

    private fun updateSaveButtonState() {
        val isInLibrary = UserSession.userId?.let { repo.isCardsetInLibrary(it, cardsetId) }

        if (isInLibrary == true) {
            saveButton.setImageResource(R.drawable.ic_bookmark_filled)
            saveButton.contentDescription = getString(R.string.remove_from_library)
        } else {
            saveButton.setImageResource(R.drawable.ic_bookmark_outline)
            saveButton.contentDescription = getString(R.string.add_to_library)
        }
    }

    private fun setupCardset(cardset: Cardset) {
        cardsetTitle.text = cardset.name

        val isOwner = UserSession.userId == cardset.userRef.toLong()
        val btnEditSet = requireView().findViewById<ImageButton>(R.id.buttonEditSet)
        val btnDeleteSet = requireView().findViewById<ImageButton>(R.id.buttonDeleteSet)

        btnEditSet.visibility = if (isOwner) View.VISIBLE else View.GONE
        btnDeleteSet.visibility = if (isOwner) View.VISIBLE else View.GONE
        privacyLabel.visibility = if (isOwner) View.VISIBLE else View.GONE
        privacySwitch.visibility = if (isOwner) View.VISIBLE else View.GONE
        privacySwitch.isChecked = !cardset.isPublic

        if (isOwner) {
            setupOwnerControls(cardset, btnEditSet, btnDeleteSet)
        }

        val cards = repo.getCardsForCardset(cardset.id)
        viewPager.adapter = CardAdapter(
            cards = cards,
            isOwner = isOwner,
            onEditCard = { card -> showCardEditDialog(card, false) },
            onDeleteCard = { card -> showDeleteCardDialog(card) },
            onAddCard = { showCardEditDialog(isNewCard = true) }
        )
    }

    private fun setupOwnerControls(cardset: Cardset, btnEditSet: ImageButton, btnDeleteSet: ImageButton) {
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

        privacySwitch.setOnCheckedChangeListener { _, _ ->
            repo.switchCardsetPrivacy(cardset.id)
            val updated = repo.getCardsetById(cardset.id)!!
            privacySwitch.isChecked = !updated.isPublic
        }
    }

    private fun showCardEditDialog(card: Card? = null, isNewCard: Boolean = false) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_card, null)
        val editTextEn = dialogView.findViewById<EditText>(R.id.editTextWordEn)
        val editTextUa = dialogView.findViewById<EditText>(R.id.editTextWordUa)
        val editTextNote = dialogView.findViewById<EditText>(R.id.editTextNote)

        if (card != null) {
            editTextEn.setText(card.wordEn)
            editTextUa.setText(card.wordUa)
            editTextNote.setText(card.note)
        }

        val titleRes = if (isNewCard) "Додати нову картку" else "Змінити картку"
        val buttonRes = if (isNewCard) "Додати" else "Зберегти"

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(titleRes)
            .setView(dialogView)
            .setPositiveButton(buttonRes, null)
            .setNegativeButton("Назад", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val wordEn = editTextEn.text.toString().trim()
                val wordUa = editTextUa.text.toString().trim()
                val note = editTextNote.text.toString().trim()

                val enValidation = validationService.validateEnglishWord(wordEn)
                val uaValidation = validationService.validateUkrainianWord(wordUa)

                when {
                    !enValidation.isValid -> {
                        Toast.makeText(requireContext(), enValidation.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    !uaValidation.isValid -> {
                        Toast.makeText(requireContext(), uaValidation.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        if (isNewCard) {
                            repo.addCard(
                                Card(
                                    id = 0,
                                    cardsetRef = cardsetId,
                                    wordEn = wordEn,
                                    wordUa = wordUa,
                                    note = note
                                )
                            )
                        } else {
                            repo.updateCard(card!!.copy(
                                wordEn = wordEn,
                                wordUa = wordUa,
                                note = note
                            ))
                        }
                        setupCardset(repo.getCardsetById(cardsetId)!!)
                        dialog.dismiss()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteCardDialog(card: Card) {
        AlertDialog.Builder(requireContext())
            .setTitle("Видалити картку?")
            .setMessage("Ви впевнені, що хочете видалити цю картку?")
            .setPositiveButton("Видалити") { _, _ ->
                repo.deleteCard(card.id)
                setupCardset(repo.getCardsetById(cardsetId)!!)
            }
            .setNegativeButton("Назад", null)
            .show()
    }

    private fun copyToClipboard(text: String, label: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Скопійовано: $text", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val cards = repo.getCardsForCardset(cardsetId)

        if (currentCardPosition >= cards.size) {
            return
        }

        val currentCard = cards[currentCardPosition]
        val isOwner = UserSession.userId == repo.getCardsetById(cardsetId)?.userRef?.toLong()

        menu.setHeaderTitle("Опції для картки")

        menu.add(0, 1, 0, "Копіювати англійське слово")
        menu.add(0, 2, 0, "Копіювати український переклад")

        if (isOwner) {
            menu.add(0, 3, 0, "Редагувати картку")
            menu.add(0, 4, 0, "Видалити картку")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val cards = repo.getCardsForCardset(cardsetId)

        if (currentCardPosition >= cards.size) {
            return super.onContextItemSelected(item)
        }

        val currentCard = cards[currentCardPosition]

        return when (item.itemId) {
            1 -> {
                copyToClipboard(currentCard.wordEn, "English Word")
                true
            }
            2 -> {
                copyToClipboard(currentCard.wordUa, "Ukrainian Translation")
                true
            }
            3 -> {
                showCardEditDialog(currentCard, false)
                true
            }
            4 -> {
                showDeleteCardDialog(currentCard)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}