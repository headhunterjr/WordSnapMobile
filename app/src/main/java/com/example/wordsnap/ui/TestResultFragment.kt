package com.example.wordsnap.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.wordsnap.R

class TestResultFragment : Fragment(R.layout.fragment_test_result) {
    companion object {
        private const val ARG_KNOWN  = "known"
        private const val ARG_TOTAL  = "total"
        private const val ARG_CS_ID  = "cardset_id"
        private const val ARG_NEW_RATE = "new_rate"
        private const val ARG_BEST_RATE = "best_rate"
        fun newInstance(known: Int, total: Int, newRate: Double, bestRate: Double,  csId: Int) = TestResultFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_KNOWN, known)
                putInt(ARG_TOTAL, total)
                putDouble(ARG_NEW_RATE, newRate)
                putDouble(ARG_BEST_RATE, bestRate)
                putInt(ARG_CS_ID, csId)
            }
        }
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        val known = requireArguments().getInt(ARG_KNOWN)
        val total = requireArguments().getInt(ARG_TOTAL)
        val csId  = requireArguments().getInt(ARG_CS_ID)
        val newRate  = requireArguments().getDouble(ARG_NEW_RATE)
        val bestRate = requireArguments().getDouble(ARG_BEST_RATE)
        v.findViewById<TextView>(R.id.textScore)
            .text = getString(R.string.test_score_format, known, total)
        v.findViewById<TextView>(R.id.textNewScore).text =
            getString(R.string.new_score_percent_format, newRate * 100f)

        v.findViewById<TextView>(R.id.textBestScore).text =
            getString(R.string.best_score_percent_format, bestRate * 100f)

        v.findViewById<Button>(R.id.buttonYes).setOnClickListener {
            parentFragmentManager.popBackStack()
            parentFragmentManager.popBackStack()
        }

        v.findViewById<Button>(R.id.buttonNo).setOnClickListener {
            parentFragmentManager.popBackStack()
            parentFragmentManager.popBackStack()
            parentFragmentManager.popBackStack()
        }
    }
}
