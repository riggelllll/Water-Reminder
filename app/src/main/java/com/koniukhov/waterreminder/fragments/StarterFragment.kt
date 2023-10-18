package com.koniukhov.waterreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.koniukhov.waterreminder.R
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.StarterFragmentBinding
import com.koniukhov.waterreminder.viewmodels.StarterViewModel

class StarterFragment : Fragment() {

    private var _binding: StarterFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StarterViewModel by viewModels{StarterViewModel.StarterViewModelFactory(
        UserDataStore(requireContext().dataStore), requireActivity().application)}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StarterFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.hasToNavigate.observe(viewLifecycleOwner) {
            if (it) {
                navigateToHome()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToHome(){
        findNavController().navigate(R.id.action_starterFragment_to_homeFragment)
    }
}