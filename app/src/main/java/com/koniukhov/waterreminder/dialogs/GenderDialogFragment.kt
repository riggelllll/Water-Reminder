package com.koniukhov.waterreminder.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.koniukhov.waterreminder.data.user.Gender
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.GenderDialogFragmentBinding
import com.koniukhov.waterreminder.utilities.WaterHelper
import com.koniukhov.waterreminder.viewmodels.MainViewModel

class GenderDialogFragment : DialogFragment() {

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
        if (sharedViewModel.userPreferences.gender == Gender.MALE){
            binding.maleRadio.isChecked = true
        }else{
            binding.femaleRadio.isChecked = true
        }

        binding.cancelBtn.setOnClickListener{
            dismiss()
        }

        binding.saveBtn.setOnClickListener{
            if (binding.maleRadio.isChecked){
                sharedViewModel.changeGender(Gender.MALE)
                sharedViewModel.changeWaterLimit(WaterHelper.calculateWaterAmount(Gender.MALE, sharedViewModel.userPreferences.weight))
            }else{
                sharedViewModel.changeGender(Gender.FEMALE)
                sharedViewModel.changeWaterLimit(WaterHelper.calculateWaterAmount(Gender.FEMALE, sharedViewModel.userPreferences.weight))
            }
            dismiss()
        }
    }

    companion object {
        const val TAG = "GenderDialog"
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}