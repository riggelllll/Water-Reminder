package com.koniukhov.waterreminder.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.WeightDialogFragmentBinding
import com.koniukhov.waterreminder.utilities.WaterHelper
import com.koniukhov.waterreminder.viewmodels.MainViewModel

class WeightDialogFragment : DialogFragment() {

    private var _binding: WeightDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory(UserDataStore(requireContext().dataStore), requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WeightDialogFragmentBinding.inflate(inflater, container, false)
        this.dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.slider.value = sharedViewModel.userPreferences.weight.toFloat()

        binding.cancelBtn.setOnClickListener{
            dismiss()
        }

        binding.saveBtn.setOnClickListener{
            val weight = binding.slider.value.toInt()
            val waterLimit = WaterHelper.calculateWaterAmount(sharedViewModel.userPreferences.gender, weight)
            sharedViewModel.changeWeight(weight)
            sharedViewModel.changeWaterLimit(waterLimit)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "WeightDialog"
    }
}