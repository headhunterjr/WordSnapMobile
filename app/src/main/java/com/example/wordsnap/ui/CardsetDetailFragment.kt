package com.example.wordsnap.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
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

        val cardset = repo.getCardsetById(cardsetId)
        if (cardset != null) {
            setupCardset(cardset)
        } else {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val btnTest = view.findViewById<Button>(R.id.buttonTakeTest)
        if (UserSession.isLoggedIn) {
            btnTest.visibility = View.VISIBLE
            btnTest.setOnClickListener {
                val frag = TestFragment.newInstance(cardset!!.id)
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
        cardsetTitle.text = cardset.name

        val cards = repo.getCardsForCardset(cardset.id)

        val adapter = CardAdapter(cards)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
    }
}