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
        fun newInstance(known: Int, total: Int, csId: Long) = TestResultFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_KNOWN, known)
                putInt(ARG_TOTAL, total)
                putLong(ARG_CS_ID, csId)
            }
        }
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        val known = requireArguments().getInt(ARG_KNOWN)
        val total = requireArguments().getInt(ARG_TOTAL)
        val csId  = requireArguments().getInt(ARG_CS_ID)

        v.findViewById<TextView>(R.id.textScore)
            .text = getString(R.string.test_score_format, known, total)

        v.findViewById<Button>(R.id.buttonYes).setOnClickListener {
            parentFragmentManager.popBackStack()
            parentFragmentManager.popBackStack()
        }

        v.findViewById<Button>(R.id.buttonNo).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack(
                "HOME",
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }
}
