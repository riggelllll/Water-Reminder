package com.koniukhov.waterreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.koniukhov.waterreminder.adapters.DailyStatisticsAdapter
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.DailyStatisticsFragmentBinding
import com.koniukhov.waterreminder.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyStatisticsFragment : Fragment() {

    private var _binding: DailyStatisticsFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory(UserDataStore(requireContext().dataStore), requireActivity().application)
    }

    private lateinit var adapter: DailyStatisticsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DailyStatisticsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        observeDailyWater()
        showHint()
    }

    private fun initAdapter(){
        adapter = DailyStatisticsAdapter(requireContext(), sharedViewModel)
        binding.recyclerView.adapter = adapter
    }

    private fun observeDailyWater(){
        lifecycleScope.launch(Dispatchers.Main){
            sharedViewModel.allDailyWater.collect{
                adapter.submitList(it.asReversed())
            }
        }
    }

    private fun showHint(){
        if (sharedViewModel.waterAmount.value!! > 0){
            binding.hint.visibility = View.INVISIBLE
        }else{
            binding.hint.visibility = View.VISIBLE
        }
    }
}