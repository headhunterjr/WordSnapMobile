package com.example.wordsnap.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.data.entities.Card
import com.example.wordsnap.R

class CardAdapter(
    private val cards: List<Card>,
    private val isOwner: Boolean = false,
    private val onEditCard: (Card) -> Unit = {},
    private val onDeleteCard: (Card) -> Unit = {},
    private val onAddCard: () -> Unit = {}
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun getItemCount() = cards.size + if (isOwner) 1 else 0

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val front = itemView.findViewById<View>(R.id.cardFrontLayout)
        private val back = itemView.findViewById<View>(R.id.cardBackLayout)
        private val frontText = itemView.findViewById<TextView>(R.id.textViewCardFront)
        private val backText = itemView.findViewById<TextView>(R.id.textViewCardBack)
        private val noteText  = itemView.findViewById<TextView>(R.id.textViewCardNote)
        private val btnEdit = itemView.findViewById<ImageButton>(R.id.buttonEditCard)
        private val btnDelete = itemView.findViewById<ImageButton>(R.id.buttonDeleteCard)
        private val btnEditBack = itemView.findViewById<ImageButton>(R.id.buttonEditCardBack)
        private val btnDeleteBack = itemView.findViewById<ImageButton>(R.id.buttonDeleteCardBack)
        private val cardContainer = itemView.findViewById<View>(R.id.cardContainer)
        private val addContainer = itemView.findViewById<View>(R.id.addContainer)
        private val btnAdd = itemView.findViewById<ImageButton>(R.id.buttonAddCard)

        private var isFrontVisible = true
        private val frontAnim: AnimatorSet =
            AnimatorInflater.loadAnimator(itemView.context, R.animator.front_card_flip) as AnimatorSet
        private val backAnim : AnimatorSet =
            AnimatorInflater.loadAnimator(itemView.context, R.animator.back_card_flip)  as AnimatorSet

        init {
            val scale = itemView.context.resources.displayMetrics.density
            val distance = 8000 * scale
            front.cameraDistance = distance
            back.cameraDistance = distance
        }

        fun bindRealCard(card: Card) {
            cardContainer.visibility = View.VISIBLE
            addContainer.visibility = View.GONE

            front.visibility = View.VISIBLE
            back.visibility = View.INVISIBLE
            front.rotationY = 0f
            back.rotationY = 90f
            isFrontVisible = true

            frontText.text = card.wordEn
            backText.text = card.wordUa
            if (card.note.isBlank()) {
                noteText.visibility = View.GONE
            } else {
                noteText.visibility = View.VISIBLE
                noteText.text = card.note
            }

            val buttonsVisibility = if (isOwner) View.VISIBLE else View.GONE
            btnEdit.visibility = buttonsVisibility
            btnDelete.visibility = buttonsVisibility
            btnEditBack.visibility = buttonsVisibility
            btnDeleteBack.visibility = buttonsVisibility

            itemView.setOnClickListener { flipCard() }

            val editClickListener = View.OnClickListener { onEditCard(card) }
            val deleteClickListener = View.OnClickListener { onDeleteCard(card) }

            btnEdit.setOnClickListener(editClickListener)
            btnEditBack.setOnClickListener(editClickListener)
            btnDelete.setOnClickListener(deleteClickListener)
            btnDeleteBack.setOnClickListener(deleteClickListener)
        }

        fun bindAddStub() {
            cardContainer.visibility = View.GONE
            addContainer.visibility = View.VISIBLE
            btnAdd.setOnClickListener { onAddCard() }
        }

        private fun flipCard() {
            if (isFrontVisible) {
                back.visibility = View.VISIBLE
                frontAnim.setTarget(front)
                backAnim.setTarget(back)
                frontAnim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        front.visibility = View.INVISIBLE
                        frontAnim.removeListener(this)
                    }
                })
                frontAnim.start(); backAnim.start()
            } else {
                front.visibility = View.VISIBLE
                frontAnim.setTarget(back)
                backAnim.setTarget(front)
                frontAnim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        back.visibility = View.INVISIBLE
                        frontAnim.removeListener(this)
                    }
                })
                frontAnim.start(); backAnim.start()
            }
            isFrontVisible = !isFrontVisible
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(v)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        if (isOwner && position == cards.size) {
            holder.bindAddStub()
        } else {
            holder.bindRealCard(cards[position])
        }
    }
}