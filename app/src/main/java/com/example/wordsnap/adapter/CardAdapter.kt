package com.example.wordsnap.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.data.entities.Card
import com.example.wordsnap.R

class CardAdapter(private val cards: List<Card>) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardFront: ConstraintLayout = itemView.findViewById(R.id.cardFrontLayout)
        val cardBack: ConstraintLayout  = itemView.findViewById(R.id.cardBackLayout)
        val frontText: TextView         = itemView.findViewById(R.id.textViewCardFront)
        val backText: TextView          = itemView.findViewById(R.id.textViewCardBack)

        var isFrontVisible = true
        private val frontAnim: AnimatorSet =
            AnimatorInflater.loadAnimator(itemView.context, R.animator.front_card_flip) as AnimatorSet
        private val backAnim : AnimatorSet =
            AnimatorInflater.loadAnimator(itemView.context, R.animator.back_card_flip)  as AnimatorSet

        init {
            val scale = itemView.context.resources.displayMetrics.density
            val distance = 8000 * scale
            cardFront.cameraDistance = distance
            cardBack.cameraDistance  = distance

            itemView.setOnClickListener { flipCard() }
        }

        fun flipCard() {
            if (isFrontVisible) {
                cardBack.visibility = View.VISIBLE

                frontAnim.setTarget(cardFront)
                backAnim.setTarget(cardBack)

                frontAnim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        cardFront.visibility = View.INVISIBLE
                        frontAnim.removeListener(this)
                    }
                })

                frontAnim.start()
                backAnim.start()
            } else {
                cardFront.visibility = View.VISIBLE

                frontAnim.setTarget(cardBack)
                backAnim.setTarget(cardFront)

                frontAnim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        cardBack.visibility = View.INVISIBLE
                        frontAnim.removeListener(this)
                    }
                })

                frontAnim.start()
                backAnim.start()
            }
            isFrontVisible = !isFrontVisible
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.frontText.text = card.wordEn
        holder.backText.text  = card.wordUa

        if (!holder.isFrontVisible) {
            holder.cardFront.visibility = View.VISIBLE
            holder.cardBack.visibility  = View.INVISIBLE
            holder.isFrontVisible       = true
        }
    }

    override fun getItemCount(): Int = cards.size
}
