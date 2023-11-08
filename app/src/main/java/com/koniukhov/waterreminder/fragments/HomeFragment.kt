package com.koniukhov.waterreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.HomeFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import androidx.lifecycle.lifecycleScope
import com.koniukhov.waterreminder.R
import com.koniukhov.waterreminder.views.WaterProgressCircle
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPreferences = UserDataStore(requireContext().dataStore)

        lifecycleScope.launch(Dispatchers.Main) {
            if (userPreferences.userPreferencesFlow.first().isFirstOpening){
                findNavController().navigate(R.id.action_homeFragment_to_starterFragment)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var p = 0
        val progress = binding.waveLoadingView
        progress.setOnClickListener {
            p += 10
            progress.setProgress(p)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}