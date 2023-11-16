package com.koniukhov.waterreminder.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.koniukhov.waterreminder.fragments.DailyStatisticsFragment
import com.koniukhov.waterreminder.fragments.MonthlyStatisticsFragment

const val ITEMS_SIZE = 2
const val DAILY_FRAGMENT = 0
class StatisticsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return ITEMS_SIZE
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            DAILY_FRAGMENT -> {
                DailyStatisticsFragment()
            }
            else -> {
                MonthlyStatisticsFragment()
            }
        }
    }
}