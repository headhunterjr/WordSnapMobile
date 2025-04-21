package com.example.wordsnap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsnap.R
import com.example.data.entities.Cardset

class CardsetAdapter(
    private val cardsets: List<Cardset>,
    private val onItemClick: (Cardset) -> Unit,
    private val onAddClick: (() -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_ADD  = 1
    }

    private val hasFooter = onAddClick != null

    override fun getItemCount() = cardsets.size + if (hasFooter) 1 else 0

    override fun getItemViewType(position: Int) =
        if (hasFooter && position == cardsets.size) TYPE_ADD else TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cardset, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val view = holder.itemView
        val cardView     = view as CardView
        val titleText    = view.findViewById<TextView>(R.id.textViewCardsetName)
        val plusIcon     = view.findViewById<ImageView>(R.id.imagePlus)

        if (getItemViewType(position) == TYPE_ITEM) {
            val cs = cardsets[position]
            titleText.visibility = View.VISIBLE
            titleText.text       = cs.name
            plusIcon.visibility  = View.GONE
            cardView.setOnClickListener { onItemClick(cs) }

        } else {
            titleText.visibility = View.GONE
            plusIcon.visibility  = View.VISIBLE
            cardView.setOnClickListener { onAddClick?.invoke() }
        }
    }
}
