package com.koniukhov.waterreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.koniukhov.waterreminder.R
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.CustomWaterVolumeDialogFragmentBinding
import com.koniukhov.waterreminder.viewmodels.MainViewModel
import java.time.LocalDate
import java.time.LocalTime

class CustomWaterVolumeDialogFragment : DialogFragment() {    

    private var _binding: CustomWaterVolumeDialogFragmentBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory(UserDataStore(requireContext().dataStore), requireActivity().application)
    }
    
    val time: LocalTime = LocalTime.now()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CustomWaterVolumeDialogFragmentBinding.inflate(inflater, container, false)
        this.dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.viewModel = sharedViewModel
        binding.fragment = this
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun saveWater(){
        val time = LocalTime.of(binding.wakeUpHour.value, binding.wakeUpMinute.value)
        val waterAmountText = binding.waterAmount.text.toString()
        val waterAmount = when{
            waterAmountText.isEmpty() -> 0
            else -> waterAmountText.toInt()
        }
        val date = LocalDate.now()
        val iconName = resources.getResourceEntryName(R.drawable.ic_drink_ware_custom)

        if (waterAmount > 0){
            sharedViewModel.addWater(time, date, waterAmount, iconName)
        }else{
            Toast.makeText(requireContext(), R.string.toast_enter_amount_of_water, Toast.LENGTH_SHORT).show()
        }

        dismiss()
    }

    fun dismissDialog(){
        dismiss()
    }

    companion object {
        const val TAG = "CustomWaterVolumeDialog"
    }
}