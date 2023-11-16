package com.koniukhov.waterreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.koniukhov.waterreminder.databinding.MonthlyStatisticsFragmentBinding

class MonthlyStatisticsFragment : Fragment() {

    private var _binding: MonthlyStatisticsFragmentBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MonthlyStatisticsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }
}