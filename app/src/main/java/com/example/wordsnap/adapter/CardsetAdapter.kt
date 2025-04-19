package com.example.wordsnap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsnap.R
import com.example.data.entities.Cardset

class CardsetAdapter(
    private val cardsets: List<Cardset>,
    private val onItemClick: (Cardset) -> Unit
) : RecyclerView.Adapter<CardsetAdapter.CardsetViewHolder>() {

    class CardsetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView as CardView
        val titleTextView: TextView = itemView.findViewById(R.id.textViewCardsetName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cardset, parent, false)
        return CardsetViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardsetViewHolder, position: Int) {
        val cardset = cardsets[position]
        holder.titleTextView.text = cardset.name

        holder.cardView.setOnClickListener {
            onItemClick(cardset)
        }
    }

    override fun getItemCount() = cardsets.size
}