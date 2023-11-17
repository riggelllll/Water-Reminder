package com.koniukhov.waterreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.koniukhov.waterreminder.R
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.MonthlyStatisticsFragmentBinding
import com.koniukhov.waterreminder.utilities.getPercentageOfWaterDrunk
import com.koniukhov.waterreminder.viewmodels.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate


class MonthlyStatisticsFragment : Fragment() {

    private var _binding: MonthlyStatisticsFragmentBinding? = null
    val binding get() = _binding!!

    private val sharedViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory(UserDataStore(requireContext().dataStore), requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MonthlyStatisticsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initChart()
        setDataToChart()
    }

    private fun initChart(){
        val chart = binding.chart
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.axisLeft.axisMinimum = 0f
        chart.axisLeft.axisMaximum = 100f
        chart.axisLeft.labelCount = 5
        chart.axisLeft.valueFormatter = PercentageFormatter
        chart.setDrawGridBackground(false)
        chart.setDrawBarShadow(false)
        chart.setDrawBorders(false)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.labelCount = 15
    }

    private fun setDataToChart(){
        val chart = binding.chart
        val entries: ArrayList<BarEntry> = ArrayList()
        lifecycleScope.launch {
            sharedViewModel.allMonthlyWater.collect{
                it.groupBy { v -> v.date.takeLast(2).toInt() }.mapValues {entry ->
                    entry.value.sumOf { e -> e.volume }
                }.forEach{f->
                    val waterLimit = sharedViewModel.userFlow.first().waterLimitPerDay
                    entries.add(BarEntry(f.key.toFloat(), getPercentageOfWaterDrunk(f.value, waterLimit).toFloat()))
                }

                val numDays = LocalDate.now().lengthOfMonth()
                for (i in 1..numDays){
                    entries.add(BarEntry(i.toFloat(), 0f))
                }

                val dataSet = BarDataSet(entries, "Label")
                dataSet.setDrawValues(false)
                dataSet.color = requireContext().getColor(R.color.md_theme_dark_inversePrimary)
                val data = BarData(dataSet)
                chart.data = data
                chart.invalidate()
            }
        }
    }
    companion object PercentageFormatter : ValueFormatter(){
        override fun getFormattedValue(value: Float): String {
            return "$value %"
        }
    }
}