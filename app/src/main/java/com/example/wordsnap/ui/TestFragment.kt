package com.example.wordsnap.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.wordsnap.R
import com.example.data.entities.Card
import com.example.domain.repository.WordSnapRepositoryImplementation

class TestFragment : Fragment(R.layout.fragment_test) {
    private lateinit var cards: List<Card>
    private var index = 0
    private var knownCount = 0

    companion object {
        private const val ARG_CS_ID = "cardset_id"
        fun newInstance(csId: Int) = TestFragment().apply {
            arguments = Bundle().apply { putInt(ARG_CS_ID, csId) }
        }
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        val csId = requireArguments().getInt(ARG_CS_ID)
        cards = WordSnapRepositoryImplementation(requireContext()).getCardsForCardset(csId)
        showCard(v)
        v.findViewById<Button>(R.id.buttonKnow).setOnClickListener {
            knownCount++; nextOrFinish(v)
        }
        v.findViewById<Button>(R.id.buttonDontKnow).setOnClickListener {
            nextOrFinish(v)
        }
        v.findViewById<ImageButton>(R.id.buttonBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showCard(v: View) {
        v.findViewById<TextView>(R.id.textCardWord)
            .text = cards[index].wordEn
    }

    private fun nextOrFinish(v: View) {
        index++
        if (index < cards.size) {
            showCard(v)
        } else {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,
                    TestResultFragment.newInstance(knownCount, cards.size,
                        requireArguments().getLong(ARG_CS_ID)))
                .addToBackStack(null)
                .commit()
        }
    }
}
