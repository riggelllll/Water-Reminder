package com.koniukhov.waterreminder.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.RemindIntervalDialogFragmentBinding
import com.koniukhov.waterreminder.viewmodels.MainViewModel

class ReminderIntervalDialogFragment : DialogFragment() {

    private var _binding: RemindIntervalDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory(UserDataStore(requireContext().dataStore), requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RemindIntervalDialogFragmentBinding.inflate(inflater, container, false)
        this.dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSlider()
        addCancelBtnListener()
        addSaveBtnListener()
    }

    private fun addSaveBtnListener() {
        binding.saveBtn.setOnClickListener {
            sharedViewModel.changeReminderInterval(binding.slider.value.toInt())
            dismiss()
        }
    }

    private fun addCancelBtnListener() {
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun initSlider() {
        binding.slider.value = sharedViewModel.userPreferences.reminderInterval.toFloat()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ReminderIntervalDialog"
    }

}