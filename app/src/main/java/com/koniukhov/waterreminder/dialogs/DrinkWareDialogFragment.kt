package com.koniukhov.waterreminder.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.koniukhov.waterreminder.adapters.DrinkWareAdapter
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.DrinkWareDialogFragmentBinding
import com.koniukhov.waterreminder.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrinkWareDialogFragment : DialogFragment() {

    private var _binding: DrinkWareDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory(UserDataStore(requireContext().dataStore), requireActivity().application)
    }

    private lateinit var adapter: DrinkWareAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DrinkWareDialogFragmentBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        addCloseBtnListener()
    }

    private fun addCloseBtnListener() {
        binding.closeBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun initAdapter() {
        adapter = DrinkWareAdapter(requireContext(), sharedViewModel)
        binding.drinkWareRecycler.adapter = adapter
        lifecycleScope.launch {
            sharedViewModel.allDrinkWare.collect {
                adapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object{
        const val TAG = "DrinkWareDialog"
    }
}