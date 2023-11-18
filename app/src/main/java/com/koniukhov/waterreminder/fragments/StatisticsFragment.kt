package com.koniukhov.waterreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.koniukhov.waterreminder.adapters.StatisticsAdapter
import com.koniukhov.waterreminder.databinding.StatisticsFragmentBinding

const val TAB_POS_EXTRA = "TAB_POS"

class StatisticsFragment : Fragment() {
    private var tabPos = 0
    private var _binding: StatisticsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StatisticsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        addTabListener()

        savedInstanceState?.let {
            tabPos = it.getInt(TAB_POS_EXTRA)
            binding.tabLayout.getTabAt(tabPos)?.select()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.tabLayout.clearOnTabSelectedListeners()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TAB_POS_EXTRA, tabPos)
    }

    private fun initViewPager(){
        binding.viewPager.apply {
            adapter = StatisticsAdapter(this@StatisticsFragment)
            isUserInputEnabled = false
        }
    }

    private fun addTabListener(){
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab!!.position
                tabPos = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }
}