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
        private val btnEdit = itemView.findViewById<ImageButton>(R.id.buttonEditCard)
        private val btnDelete = itemView.findViewById<ImageButton>(R.id.buttonDeleteCard)
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
            back .cameraDistance = distance
        }

        fun bindRealCard(card: Card) {
            front.visibility = View.VISIBLE
            back .visibility = View.INVISIBLE
            isFrontVisible   = true
            frontText.text = card.wordEn
            backText .text = card.wordUa
            btnEdit.visibility   = if (isOwner) View.VISIBLE else View.GONE
            btnDelete.visibility = if (isOwner) View.VISIBLE else View.GONE
            btnAdd.visibility    = View.GONE

            itemView.setOnClickListener { flipCard() }
            btnEdit   .setOnClickListener { onEditCard(card) }
            btnDelete .setOnClickListener { onDeleteCard(card) }
        }

        fun bindAddStub() {
            front.visibility = View.INVISIBLE
            back .visibility = View.INVISIBLE
            btnEdit   .visibility = View.GONE
            btnDelete .visibility = View.GONE

            btnAdd.visibility = View.VISIBLE
            btnAdd.setOnClickListener { onAddCard() }
        }

        private fun flipCard() {
            if (isFrontVisible) {
                back.visibility = View.VISIBLE
                frontAnim.setTarget(front)
                backAnim .setTarget(back)
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
                backAnim .setTarget(front)
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
