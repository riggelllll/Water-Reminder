package com.koniukhov.waterreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.koniukhov.waterreminder.data.user.Sex
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.GenderDialogFragmentBinding
import com.koniukhov.waterreminder.utilities.WaterHelper
import com.koniukhov.waterreminder.viewmodels.MainViewModel

class GenderDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "GenderDialog"
    }

    private var _binding: GenderDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory(UserDataStore(requireContext().dataStore), requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GenderDialogFragmentBinding.inflate(inflater, container, false)
        this.dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (sharedViewModel.userPreferences.sex == Sex.MALE){
            binding.maleRadio.isChecked = true
        }else{
            binding.femaleRadio.isChecked = true
        }

        binding.cancelBtn.setOnClickListener{
            dismiss()
        }

        binding.saveBtn.setOnClickListener{
            if (binding.maleRadio.isChecked){
                sharedViewModel.changeGender(Sex.MALE)
                sharedViewModel.changeWaterLimit(WaterHelper.calculateWaterAmount(Sex.MALE, sharedViewModel.userPreferences.weight))
            }else{
                sharedViewModel.changeGender(Sex.FEMALE)
                sharedViewModel.changeWaterLimit(WaterHelper.calculateWaterAmount(Sex.FEMALE, sharedViewModel.userPreferences.weight))
            }
            dismiss()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}