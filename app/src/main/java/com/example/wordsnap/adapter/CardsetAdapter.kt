package com.example.wordsnap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsnap.R
import com.example.wordsnap.entities.Cardset

class CardsetAdapter(
    private val cardsets: List<Cardset>,
    private val onItemClick: (Cardset) -> Unit
) : RecyclerView.Adapter<CardsetAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewCardsetName: TextView = itemView.findViewById(R.id.textViewCardsetName)

        fun bind(cardset: Cardset) {
            textViewCardsetName.text = cardset.name

            // Handle clicks
            itemView.setOnClickListener {
                onItemClick(cardset)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cardset, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardset = cardsets[position]
        holder.bind(cardset)
    }

    override fun getItemCount() = cardsets.size
}
